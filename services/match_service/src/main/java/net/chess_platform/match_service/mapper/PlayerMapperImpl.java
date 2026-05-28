package net.chess_platform.match_service.mapper;

import javax.annotation.processing.Generated;

import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.user.UserEventData;
import net.chess_platform.common.dto.chess.MatchResultDto;
import net.chess_platform.match_service.model.Player;

@Generated(value = "org.mapstruct.ap.MappingProcessor", date = "2026-05-28T13:25:52+0200", comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 23 (Oracle Corporation)")
@Component
public class PlayerMapperImpl implements PlayerMapper {

    @Override
    public Player.Update toUpdate(UserEventData dto) {
        if (dto == null) {
            return null;
        }

        Player.Update update = new Player.Update();

        update.setId(dto.getId());
        update.setDisplayName(dto.getDisplayName());
        update.setAvatar(dto.getAvatar());

        return update;
    }

    @Override
    public Player.Update toUpdate(MatchResultDto.PlayerDto dto) {
        if (dto == null) {
            return null;
        }

        Player.Update update = new Player.Update();

        update.setId(dto.id());

        return update;
    }
}
