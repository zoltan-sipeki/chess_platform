package net.chess_platform.chat_service.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.RelationshipAuthorizationService;
import net.chess_platform.chat_service.dto.RelationshipDto;
import net.chess_platform.chat_service.dto.RelationshipDto.Relationship;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.chat_service.repository.FriendRepository;
import net.chess_platform.common.security.CurrentUser;

@Service
public class RelationshipService {

    private final FriendRepository friendRepository;

    private final ChannelRepository channelRepository;

    private final RelationshipAuthorizationService authService;

    public RelationshipService(FriendRepository friendRepository, ChannelRepository channelRepository,
            RelationshipAuthorizationService authService) {
        this.friendRepository = friendRepository;
        this.channelRepository = channelRepository;
        this.authService = authService;
    }

    public RelationshipDto queryRelationship(UUID userId1, UUID userId2, CurrentUser user) {
        var auth = authService.authorizeRelationshipQuery(user);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        if (userId1.equals(userId2)) {
            return new RelationshipDto(Relationship.SELF);
        }

        var friends = friendRepository.areFriends(userId1, userId2);

        return new RelationshipDto(
                friends ? Relationship.FRIENDS : Relationship.NOT_RELATED);
    }

    public Set<UUID> findContacts(UUID userId, CurrentUser user) {
        var auth = authService.authorizeContactsQuery(user);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        var friends = friendRepository.findAll(userId);

        var channels = channelRepository.findAll(userId);

        var contacts = friends.stream().map(f -> f.getFriend().getId()).collect(Collectors.toSet());

        for (var channel : channels) {
            for (var member : channel.getMemberIds()) {
                if (!member.equals(userId)) {
                    contacts.add(member);
                }
            }

        }
        
        return contacts;
    }

}
