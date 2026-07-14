package net.chess_platform.match_service.mapper;

import javax.annotation.processing.Generated;
import net.chess_platform.common.domain_events.broker.user.UserEventData;
import net.chess_platform.match_service.model.Player;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-08T16:55:39+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 23 (Oracle Corporation)"
)
@Component
public class PlayerMapperImpl implements PlayerMapper {

    @Override
    public Player.Update toUpdate(UserEventData dto) {
        if ( dto == null ) {
            return null;
        }

        Player.Update update = new Player.Update();

        update.setId( dto.id() );
        update.setDisplayName( dto.displayName() );
        update.setAvatar( dto.avatar() );

        return update;
    }
}
