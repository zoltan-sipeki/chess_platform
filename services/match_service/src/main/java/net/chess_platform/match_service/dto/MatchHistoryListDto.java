package net.chess_platform.match_service.dto;

import java.util.List;

public record MatchHistoryListDto(
    long total,
    List<MatchHistoryDto> matches
) {

}
