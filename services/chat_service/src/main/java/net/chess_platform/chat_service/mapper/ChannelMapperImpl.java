package net.chess_platform.chat_service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.common.dto.chat.ChannelDto;
import net.chess_platform.common.dto.chat.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-23T20:57:42+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.1 (Oracle Corporation)"
)
@Component
public class ChannelMapperImpl implements ChannelMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ChannelDto toDto(Channel channel) {
        if ( channel == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String type = null;
        List<UserDto> members = null;

        id = channel.getId();
        name = channel.getName();
        if ( channel.getType() != null ) {
            type = channel.getType().name();
        }
        members = userMapper.toDtoList( channel.getMembers() );

        ChannelDto channelDto = new ChannelDto( id, name, type, members );

        return channelDto;
    }

    @Override
    public List<ChannelDto> toDtoList(List<Channel> channels) {
        if ( channels == null ) {
            return null;
        }

        List<ChannelDto> list = new ArrayList<ChannelDto>( channels.size() );
        for ( Channel channel : channels ) {
            list.add( toDto( channel ) );
        }

        return list;
    }
}
