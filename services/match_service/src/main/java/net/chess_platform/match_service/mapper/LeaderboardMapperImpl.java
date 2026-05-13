package net.chess_platform.match_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.match_service.dto.LeaderboardEntryDto;
import net.chess_platform.match_service.dto.UserDto;
import net.chess_platform.match_service.model.Leaderboard;
import net.chess_platform.match_service.model.MatchUser;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2026-05-12T15:54:06+0200", comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)")
@Component
public class LeaderboardMapperImpl implements LeaderboardMapper {

    @Override
    public LeaderboardEntryDto toDto(Leaderboard leaderboard) {
        if (leaderboard == null) {
            return null;
        }

        UserDto user = null;
        int rank = 0;
        int rankedMmr = 0;
        float percentile = 0.0f;

        user = matchUserToUserDto(leaderboard.getUser());
        rank = leaderboard.getRank();
        rankedMmr = leaderboard.getRankedMmr();
        percentile = leaderboard.getPercentile();

        LeaderboardEntryDto leaderboardEntryDto = new LeaderboardEntryDto(user, rank, rankedMmr, percentile);

        return leaderboardEntryDto;
    }

    @Override
    public List<LeaderboardEntryDto> toDtoList(List<Leaderboard> leaderboards) {
        if (leaderboards == null) {
            return null;
        }

        List<LeaderboardEntryDto> list = new ArrayList<LeaderboardEntryDto>(leaderboards.size());
        for (Leaderboard leaderboard : leaderboards) {
            list.add(toDto(leaderboard));
        }

        return list;
    }

    protected UserDto matchUserToUserDto(MatchUser matchUser) {
        if (matchUser == null) {
            return null;
        }

        UUID id = null;
        String displayName = null;

        id = matchUser.getId();
        displayName = matchUser.getDisplayName();

        String username = null;

        UserDto userDto = new UserDto(id, username, displayName);

        return userDto;
    }
}
