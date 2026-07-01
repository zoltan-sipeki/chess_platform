package net.chess_platform.matchmaking_api_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.matchmaking_api_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_api_service.dto.PlayerDto;
import net.chess_platform.matchmaking_api_service.model.MatchRouting;
import net.chess_platform.matchmaking_api_service.model.Player;

// @Mapper(componentModel = "spring")
public interface MatchRoutingMapper {

    @Mapping(target = "status", source = "matchStatus")
    CurrentMatchDto toDto(MatchRouting routingData);

    @Mapping(target = "token", ignore = true)
    CurrentMatchDto toDtoWithoutToken(MatchRouting routingData);

    PlayerDto toDto(Player player);
}
