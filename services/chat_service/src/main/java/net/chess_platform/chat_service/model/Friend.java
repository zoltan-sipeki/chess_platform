package net.chess_platform.chat_service.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document
public class Friend extends AuditedEntity {

    @Id
    private UUID id = UUID.randomUUID();

    private UUID user;

    @DocumentReference
    private User friend;

    public Friend() {}

    public Friend(UUID userId, UUID friendId) {
        this.user = userId;
        var f = new User();
        f.setId(friendId);
        this.friend = f;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID userId) {
        this.user = userId;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

}
