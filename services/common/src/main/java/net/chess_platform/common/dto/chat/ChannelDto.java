package net.chess_platform.common.dto.chat;

import java.util.List;
import java.util.UUID;

public record ChannelDto(UUID id, String name, String type, List<UserDto> members) {

}
