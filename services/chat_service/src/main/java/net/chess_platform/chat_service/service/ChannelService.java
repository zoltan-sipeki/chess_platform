package net.chess_platform.chat_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.ChannelMapper;
import net.chess_platform.chat_service.mapper.UserMapper;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.ChannelMember.Role;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberJoinedEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberLeftEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.dto.chat.ChannelDto;
import net.chess_platform.common.security.CurrentUser;

@Service
public class ChannelService {

    private ChannelRepository channelRepository;

    private ChannelMemberRepository channelMemberRepository;

    private PermissionService permissionService;

    private DomainEventService eventService;

    private ChannelMapper channelMapper;

    private UserMapper userMapper;

    public ChannelService(ChannelRepository channelRepository,
            ChannelMemberRepository channelMemberRepository,
            PermissionService permisssionService,
            DomainEventService eventService, ChannelMapper channelMapper, UserMapper userMapper) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.permissionService = permisssionService;
        this.eventService = eventService;
        this.channelMapper = channelMapper;
        this.userMapper = userMapper;
    }

    public List<ChannelDto> getChannels(CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_QUERY, user, null);
        var channels = channelRepository.findAll(auth);
        return channelMapper.toDtoList(channels);
    }

    public ChannelDto createChannel(Channel.Type type, List<UUID> memberIds, CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_CREATE, user, null);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        if (type == Channel.Type.DM) {
            var channel = channelRepository.findDMChannelWithMembers(memberIds.get(0), user.id());
            if (channel != null) {
                return channelMapper.toDto(channel);
            }
        }

        var channel = new Channel();
        channel.setType(type);

        var members = createChannelMembers(type, channel, memberIds, user);

        channel = channelRepository.save(channel, auth);
        members = channelMemberRepository.saveAll(members, auth);

        var dto = channelMapper.toDto(channel);

        if (channel.getType() == Channel.Type.GROUP) {
            var e = new GroupChannelCreatedEvent(memberIds, dto);
            eventService.publish(e);
        }

        return dto;
    }

    public void updateLastReadMessage(UUID channelId, long lastReadMessageId,
            CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_UPDATE_LAST_READ_MESSAGE, user,
                Map.of("channelId", channelId));

        long modifiedCount = channelMemberRepository.updateLastReadMessage(channelId, lastReadMessageId, auth);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void clearChannelHistory(UUID channelId, CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_CLEAR_HISTORY, user,
                Map.of("channelId", channelId));

        long modifiedCount = channelMemberRepository.clearChannelHistory(channelId, auth);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public ChannelDto kickMember(UUID channelId, UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_KICK_MEMBER,
                user,
                Map.of("channelId", channelId, "userId", userId));

        long modifiedCount = channelMemberRepository.kickMemberFromGroupChannel(auth);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }

        channelRepository.kickMemberFromGroupChannel(channelId, userId);

        var channel = channelRepository.findChannelById(channelId);
        var recipients = channel.getMemberIds();
        recipients.add(userId);

        var event = new GroupChannelMemberLeftEvent(recipients, channelId, userId);
        eventService.publish(event);

        return channelMapper.toDto(channel);
    }

    public ChannelDto addChannelMembers(UUID channelId, List<UUID> addedMemberIds, CurrentUser user) {
        var auth = permissionService.authorize(
                Action.CHANNEL_ADD_MEMBER, user,
                Map.of("channelId", channelId));

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        var channel = channelRepository.findGroupChannelById(channelId);
        if (channel == null) {
            throw new EntityNotFoundException();
        }

        var ogMembers = List.copyOf(channel.getMemberIds());
        var joinedMembers = addMemberToGroupChannel(channel, addedMemberIds);

        channel = channelRepository.save(channel, auth);
        joinedMembers = channelMemberRepository.saveAll(joinedMembers, auth);

        var joinedEvent = new GroupChannelMemberJoinedEvent(ogMembers, channelId,
                userMapper.toDtoListFromChannelMember(joinedMembers));
        eventService.publish(joinedEvent);

        var dto = channelMapper.toDto(channel);

        var createdEvent = new GroupChannelCreatedEvent(addedMemberIds, dto);
        eventService.publish(createdEvent);

        return dto;
    }

    public void leaveChannel(UUID channelId, CurrentUser user) {
        var auth = permissionService.authorize(Action.CHANNEL_LEAVE, user, Map.of("channelId", channelId));

        long modifiedCount = channelMemberRepository.leaveGroupChannel(auth);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findChannelById(channelId);
        if (channel.getType() == Channel.Type.GROUP) {
            var event = new GroupChannelMemberLeftEvent(channel.getMemberIds(), channelId, user.id());
            eventService.publish(event);
        }
    }

    private List<ChannelMember> createChannelMembers(Channel.Type type, Channel channel, List<UUID> memberIds,
            CurrentUser user) {
        var members = new ArrayList<ChannelMember>();
        var embeddedChannel = new ChannelMember.EmbeddedChannel(channel.getId(), type);

        for (var memberId : memberIds) {
            var member = new ChannelMember();
            member.setChannel(embeddedChannel);
            member.setUserId(memberId);

            if (type == Channel.Type.GROUP) {
                member.addRole(ChannelMember.Role.MEMBER);
            }

            channel.addMember(memberId);
        }

        var member = new ChannelMember();
        member.setChannel(embeddedChannel);
        member.setUserId(user.id());

        if (type == Channel.Type.GROUP) {
            member.addRole(ChannelMember.Role.OWNER);
        }

        channel.addMember(user.id());
        members.add(member);

        return members;
    }

    private List<ChannelMember> addMemberToGroupChannel(Channel channel, List<UUID> addedMemberIds) {
        var embeddedChannel = new ChannelMember.EmbeddedChannel(channel.getId(), Channel.Type.GROUP);

        var addedMembers = new ArrayList<ChannelMember>();
        var ogMembers = channelMemberRepository.findByChannelId(channel.getId());
        for (var id : addedMemberIds) {
            var member = ogMembers.get(id);

            if (member == null) {
                var m = new ChannelMember();
                m.setChannel(embeddedChannel);
                m.setUserId(id);
                m.addRole(ChannelMember.Role.MEMBER);
                addedMembers.add(m);
                channel.addMember(id);
            } else if (member.isRemoved()) {
                member.setRemoved(false);
                member.addRole(Role.MEMBER);
                addedMembers.add(member);
                channel.addMember(id);
            }
        }

        return addedMembers;
    }

    public ChannelDto updateName(UUID channelId, String name, CurrentUser currentUser) {
        var auth = permissionService.authorize(Action.CHANNEL_UPDATE_NAME, currentUser, Map.of("channelId", channelId));
        var channel = channelRepository.updateName(name, auth);

        return channelMapper.toDto(channel);
    }

}
