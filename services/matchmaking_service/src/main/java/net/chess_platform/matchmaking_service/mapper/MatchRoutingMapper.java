package net.chess_platform.matchmaking_service.mapper;

import org.mapstruct.Mapping;

import net.chess_platform.matchmaking_service.dto.CurrentMatchDto;
import net.chess_platform.matchmaking_service.dto.PlayerDto;
import net.chess_platform.matchmaking_service.model.MatchRouting;
import net.chess_platform.matchmaking_service.model.Player;

// @Mapper(componentModel = "spring")
public interface MatchRoutingMapper {

    @Mapping(target = "status", source = "matchStatus")
    CurrentMatchDto toDto(MatchRouting routingData);
    
    @Mapping(target = "status", source = "matchStatus")
    @Mapping(target = "token", ignore = true)
    CurrentMatchDto toDtoWithoutToken(MatchRouting routingData);

    PlayerDto toDto(Player player);
}
