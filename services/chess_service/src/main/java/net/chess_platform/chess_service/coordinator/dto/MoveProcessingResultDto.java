package net.chess_platform.chess_service.coordinator.dto;

import java.util.List;

public record MoveProcessingResultDto(

        long nextTurn,

        MoveResultDto moveResult,

        List<PlayerDto> scoreboard) {

}
