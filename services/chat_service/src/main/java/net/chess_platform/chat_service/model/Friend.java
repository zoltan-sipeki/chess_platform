package net.chess_platform.chat_service.model;

import java.util.List;
import java.util.UUID;

public class Friend extends AuditedEntity {

    private UUID id = UUID.randomUUID();

    private UUID userId;

    private UUID friendId;

    private List<User> friend;

    public Friend(UUID userId, UUID friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getFriendId() {
        return friendId;
    }

    public void setFriendId(UUID friendId) {
        this.friendId = friendId;
    }

    public List<User> getFriend() {
        return friend;
    }

    public void setFriend(List<User> friend) {
        this.friend = friend;
    }

}
