package net.chess_platform.chat_service.dto;

import java.util.List;
import java.util.UUID;

public class ChannelDto {

    private UUID id;

    private String name;

    private String type;

    private Integer unreadCount;

    private List<UserDto> recipients;

    public ChannelDto() {
    }

    public ChannelDto(UUID id, String name, String type, Integer unreadCount, List<UserDto> members) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.unreadCount = unreadCount;
        this.recipients = members;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public List<UserDto> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<UserDto> members) {
        this.recipients = members;
    }
}