package net.chess_platform.chat_service.dto;

public record RelationshipDto(Relationship relationship) {

    public enum Relationship {
        SELF,
        FRIENDS,
        NOT_RELATED
    }

}
