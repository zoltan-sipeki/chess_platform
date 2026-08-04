package net.chess_platform.chat_service.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.ChannelAuthorizationService;
import net.chess_platform.chat_service.dto.ChannelDto;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.ChannelMapper;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.chat_service.repository.MessageRepository;
import net.chess_platform.common.domain_events.broker.chat.ChannelTypingEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberLeftEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    private final ChannelMemberRepository channelMemberRepository;

    private final MessageRepository messageRepository;

    private final ChannelAuthorizationService authService;

    private final DomainEventService eventService;

    private final ChannelMapper channelMapper;

    public ChannelService(ChannelRepository channelRepository,
            ChannelMemberRepository channelMemberRepository,
            MessageRepository messageRepository,
            ChannelAuthorizationService authService,
            DomainEventService eventService, ChannelMapper channelMapper) {
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.messageRepository = messageRepository;
        this.authService = authService;
        this.eventService = eventService;
        this.channelMapper = channelMapper;
    }

    public void broadcastTyping(UUID channelId, CurrentUser user) {
        var auth = authService.authorizeBroadcastTyping(user, channelId);

        if (!auth.isAllowed()) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findOne(channelId);
        if (channel == null) {
            throw new EntityNotFoundException();
        }

        channel.getMemberIds().removeIf(m -> m.equals(user.id()));

        eventService.publish(
                new ChannelTypingEvent(channel.getMemberIds(), new ChannelTypingEvent.Payload(user.id(), channelId)));
    }

    public List<ChannelDto> findChannels(CurrentUser user) {
        var auth = authService.authorizeChannelRead(user);

        MongoQueryFragment<Channel> fragment = auth.getQueryFragment(Channel.class);

        var channels = channelRepository.findAllWithMembers(fragment.getCriteria());

        if (channels.isEmpty()) {
            return new ArrayList<>();
        }

        for (var c : channels) {
            c.getMembers().removeIf(m -> m.getId().equals(user.id()));
        }

        var unreadCounts = messageRepository.countUnreadMessages(channels.stream().map(Channel::getId).toList(),
                user.id());

        var result = channelMapper.toDtoList(channels);

        for (var c : result) {
            c.setUnreadCount(unreadCounts.get(c.getId()));
        }

        return result;
    }

    public ChannelDto createChannel(Channel.Type type, List<UUID> memberIds, CurrentUser user) {
        if (type.equals(Channel.Type.DM) && memberIds.size() > 2) {
            throw new IllegalArgumentException("DM channels must have exactly 2 members");
        }

        var auth = authService.authorizeChannelCreate(user);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        if (type == Channel.Type.DM) {
            var channel = channelRepository.findOneWithMembers(Channel.Type.DM, memberIds);
            if (channel != null) {
                return channelMapper.toDto(channel);
            }
        }

        var channel = new Channel();
        channel.setType(type);

        var members = new ArrayList<ChannelMember>();
        for (var memberId : memberIds) {
            var channelMember = new ChannelMember();
            channelMember.setChannel(new ChannelMember.EmbeddedChannel(channel.getId(), type));
            channelMember.setUserId(memberId);

            if (type == Channel.Type.GROUP) {
                if (memberId.equals(user.id())) {
                    channelMember.addRole(ChannelMember.Role.OWNER);
                } else {
                    channelMember.addRole(ChannelMember.Role.MEMBER);
                }
            }

            channel.addMember(memberId);
            members.add(channelMember);
        }

        channelRepository.save(channel);
        channelMemberRepository.saveAll(members);

        channel = channelRepository.findOneWithMembers(channel.getId());
        channel.getMembers().removeIf(m -> m.getId().equals(user.id()));

        // maybe send group channel created event to the other users.

        return channelMapper.toDto(channel);
    }

    public void updateLastReadMessage(UUID channelId, long lastReadMessageId,
            CurrentUser user) {
        var auth = authService.authorizeUpdateLastReadMessage(user, channelId);

        MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);

        var update = new ChannelMember.Update();
        update.setLastReadMessageSeq(lastReadMessageId);

        long modifiedCount = channelMemberRepository.update(update, fragment.getCriteria());

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void clearChannelHistory(UUID channelId, CurrentUser user) {
        if (channelId == null) {
            throw new IllegalArgumentException();
        }

        var channel = channelRepository.findOne(channelId);

        if (channel == null) {
            throw new EntityNotFoundException();
        }

        var update = new ChannelMember.Update();
        update.setLastReadableMessageSeq(channel.getNextMessageSeq());
        update.setLastReadMessageSeq(channel.getNextMessageSeq() - 1);

        var auth = authService.authorizeClearChannelHistory(user, channelId);
        MongoQueryFragment<ChannelMember> f2 = auth.getQueryFragment(ChannelMember.class);

        long modifiedCount = channelMemberRepository.update(update, f2.getCriteria());

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }

    public void kickMember(UUID channelId, UUID userId, CurrentUser user) {
        var auth = authService.authorizeKickMember(user, channelId, userId);

        var update = new ChannelMember.Update();
        update.setRemoved(true);
        update.setRoles(new HashSet<>());

        MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);

        long modifiedCount = channelMemberRepository.update(update, fragment.getCriteria());

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findOne(channelId);

        channelRepository.removeMember(channelId, userId);

        var event = new GroupChannelMemberLeftEvent(channel.getMemberIds(),
                new GroupChannelMemberLeftEvent.Payload(channelId, userId));

        eventService.publish(event);
    }

    public void addChannelMembers(UUID channelId, List<UUID> newMembers, CurrentUser user) {
        var auth = authService.authorizeAddMember(user, channelId);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        var channel = channelRepository.findOne(Channel.Type.DM, channelId);
        if (channel == null) {
            throw new EntityNotFoundException();
        }

        var members = channelMemberRepository.findAll(channelId);

        var cms = new ArrayList<ChannelMember>();
        for (var id : newMembers) {
            var cm = members.get(id);
            if (cm == null) {
                var ec = new ChannelMember.EmbeddedChannel(channelId, Channel.Type.GROUP);
                cm = new ChannelMember();
                cm.setChannel(ec);
                cm.setUserId(id);
                cm.addRole(ChannelMember.Role.MEMBER);
                cms.add(cm);
            } else if (cm.isRemoved()) {
                cm.setRemoved(false);
                cm.addRole(ChannelMember.Role.MEMBER);
                cms.add(cm);
            }

        }

        channelMemberRepository.saveAll(cms);

        channelRepository.addMembers(channelId, newMembers);

        channel = channelRepository.findOneWithMembers(channel.getId());

        var e = new GroupChannelCreatedEvent(newMembers, channelMapper.toEventPayload(channel));

        eventService.publish(e);
    }

    public void leaveChannel(UUID channelId, CurrentUser user) {
        var auth = authService.authorizeLeaveChannel(user, channelId);

        MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);

        var update = new ChannelMember.Update();
        update.setRemoved(true);
        update.setRoles(new HashSet<>());

        long modifiedCount = channelMemberRepository.update(update, fragment.getCriteria());

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findOne(channelId);

        channelRepository.removeMember(channelId, user.id());

        var e = new GroupChannelMemberLeftEvent(channel.getMemberIds(),
                new GroupChannelMemberLeftEvent.Payload(channelId, user.id()));

        eventService.publish(e);

    }

    public void updateName(UUID channelId, String name, CurrentUser currentUser) {
        var auth = authService.authorizeUpdateChannelName(currentUser, channelId);

        MongoQueryFragment<Channel> fragment = auth.getQueryFragment(Channel.class);

        long modifiedCount = channelRepository.updateName(fragment.getCriteria(), name);

        if (modifiedCount == 0) {
            throw new EntityNotFoundException();
        }
    }
}
