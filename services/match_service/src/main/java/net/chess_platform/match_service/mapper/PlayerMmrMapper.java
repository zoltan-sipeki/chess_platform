package net.chess_platform.match_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import net.chess_platform.match_service.dto.PlayerMmrDto;
import net.chess_platform.match_service.model.PlayerMmr;

public interface PlayerMmrMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "mmr", source = "rankedMmr")
    PlayerMmrDto toDto(PlayerMmr playerMmr);
}
