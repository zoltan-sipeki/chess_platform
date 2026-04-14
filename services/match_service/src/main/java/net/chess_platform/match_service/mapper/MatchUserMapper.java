package net.chess_platform.match_service.mapper;

import org.mapstruct.Mapper;

import net.chess_platform.match_service.dto.MatchUserResponse;
import net.chess_platform.match_service.model.MatchUser;

public interface MatchUserMapper {

    public MatchUserResponse toDto(MatchUser user);
}
