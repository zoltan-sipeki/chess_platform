package net.chess_platform.user_service.dto;

import java.util.List;

public record UserSearchResultDto(
    boolean hasMore,
    List<ClientUserDto> users
) {

}
