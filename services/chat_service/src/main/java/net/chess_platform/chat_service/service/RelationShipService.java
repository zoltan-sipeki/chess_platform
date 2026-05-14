package net.chess_platform.chat_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.dto.ContactsDto;
import net.chess_platform.chat_service.dto.RelationshipDto;
import net.chess_platform.chat_service.dto.RelationshipDto.Relationship;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.common.security.CurrentUser;

@Service
public class RelationshipService {

    private final FriendRepository friendRepository;

    private final ChannelRepository channelRepository;

    private final PermissionService permissionService;

    public RelationshipService(FriendRepository friendRepository, ChannelRepository channelRepository,
            PermissionService permissionService) {
        this.friendRepository = friendRepository;
        this.channelRepository = channelRepository;
        this.permissionService = permissionService;
    }

    public RelationshipDto getRelationship(List<UUID> userIds, CurrentUser user) {
        if (userIds.isEmpty() || userIds.size() > 2) {
            throw new IllegalArgumentException();
        }

        var auth = permissionService.authorize(Action.RELATIONSHIP_QUERY, user, Map.of("userIds", userIds));
        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        switch (userIds.size()) {
        case 1:
            if (userIds.get(0).equals(user.id())) {
                return new RelationshipDto(Relationship.SELF);
            }
            break;
        case 2:
            if (userIds.get(0).equals(userIds.get(1))) {
                return new RelationshipDto(Relationship.SELF);
            }
            break;
        }

        var friends = friendRepository.findAll(auth);

        return new RelationshipDto(
                friends == null || friends.isEmpty() ? Relationship.NOT_RELATED : Relationship.FRIENDS);
    }

    public ContactsDto findContacts(UUID userId, CurrentUser user) {
        var auth = permissionService.authorize(Action.CONTACTS_QUERY_ALL, user, Map.of("userId", userId));

        var friends = friendRepository.findAll(auth);

        var channels = channelRepository.findAll(auth);

        var ids = friends.stream().map(Friend::getFriendId).collect(Collectors.toSet());

        for (var channel : channels) {
            for (var member : channel.getMemberIds()) {
                if (!member.equals(userId)) {
                    ids.add(member);
                }
            }

        }
        return new ContactsDto(userId, ids);
    }

}
