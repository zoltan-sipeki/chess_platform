package net.chess_platform.match_service.mapper;

import java.time.OffsetDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.match_service.dto.MatchUserResponse;
import net.chess_platform.match_service.model.MatchUser;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-20T22:01:56+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class MatchUserMapperImpl implements MatchUserMapper {

    @Override
    public MatchUserResponse toDto(MatchUser user) {
        if ( user == null ) {
            return null;
        }

        UUID id = null;

        id = user.getId();

        int unrankedMmr = 0;
        int rankedMmr = 0;
        OffsetDateTime lastPlayed = null;

        MatchUserResponse matchUserResponse = new MatchUserResponse( id, unrankedMmr, rankedMmr, lastPlayed );

        return matchUserResponse;
    }
}
