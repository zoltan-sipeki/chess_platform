package net.chess_platform.chat_service.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.MessageMapper;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.permission.PermissionService;
import net.chess_platform.chat_service.permission.PermissionService.Action;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.chat_service.repository.MessageRepository;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageDeletedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageEditedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.dto.chat.MessageDto;
import net.chess_platform.common.security.CurrentUser;

@Service
public class MessageService {

    private MessageRepository messageRepository;

    private PermissionService permissionService;

    private MessageMapper messageMapper;

    private DomainEventService eventService;

    private ChannelRepository channelRepository;

    public MessageService(MessageRepository messageRepository,
            PermissionService permissionService,
            MessageMapper messageMapper, DomainEventService eventService, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.permissionService = permissionService;
        this.messageMapper = messageMapper;
        this.eventService = eventService;
        this.channelRepository = channelRepository;
    }

    public MessageDto create(UUID channelId, String content, CurrentUser user) {
        var auth = permissionService.authorize(Action.MESSAGE_CREATE, user, Map.of("channelId", channelId));

        var message = new Message();
        message.setChannelId(channelId);
        message.setSenderId(user.id());
        message.setContent(content);

        message = messageRepository.save(message, auth);

        if (message == null) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findChannelById(channelId);
        eventService.publish(new MessageCreatedEvent(channel.getMemberIds(), messageMapper.toDto(message)));

        return messageMapper.toDto(message);
    }

    public List<MessageDto> findAll(UUID channelId, int limit, CurrentUser currentUser) {
        var auth = permissionService.authorize(Action.MESSAGE_QUERY, currentUser,
                Map.of("channelId", channelId));

        var messages = messageRepository.findAll(limit, auth);
        return messageMapper.toDtoList(messages);
    }

    public MessageDto updateContent(UUID channelId, long messageId, String content,
            CurrentUser user) {
        var auth = permissionService.authorize(Action.MESSAGE_UPDATE_CONTENT, user,
                Map.of("channelId", channelId, "messageId", messageId));

        var message = messageRepository.updateContent(content, auth);

        if (message == null) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findChannelById(channelId);
        eventService.publish(new MessageEditedEvent(channel.getMemberIds(), messageMapper.toDto(message)));

        return messageMapper.toDto(message);
    }

    public void delete(UUID channelId, long messageId, CurrentUser user) {
        var auth = permissionService.authorize(Action.MESSAGE_DELETE, user,
                Map.of("channelId", channelId, "messageId", messageId));

        var deletedMessage = messageRepository.delete(auth);

        if (deletedMessage == null) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findChannelById(channelId);
        eventService
                .publish(new MessageDeletedEvent(channel.getMemberIds(), messageMapper.toDto(deletedMessage)));
    }
}
