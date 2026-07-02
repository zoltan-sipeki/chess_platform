package net.chess_platform.matchmaking_service.dto;

import net.chess_platform.matchmaking_service.model.MatchRouting;

public record UpdateMatchRoutingDto(MatchRouting.Status matchStatus) {

}
