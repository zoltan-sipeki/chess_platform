package net.chess_platform.chat_service.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import net.chess_platform.chat_service.authorization.MessageAuthorizationService;
import net.chess_platform.chat_service.dto.MessageDto;
import net.chess_platform.chat_service.exception.AccessDeniedException;
import net.chess_platform.chat_service.exception.EntityNotFoundException;
import net.chess_platform.chat_service.mapper.MessageMapper;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Message;
import net.chess_platform.chat_service.repository.ChannelMemberRepository;
import net.chess_platform.chat_service.repository.ChannelRepository;
import net.chess_platform.chat_service.repository.MessageRepository;
import net.chess_platform.chat_service.repository.UserRepository;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageDeletedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageEditedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.common.permission.MongoQueryFragment;
import net.chess_platform.common.security.CurrentUser;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    private final MessageAuthorizationService authService;

    private final MessageMapper messageMapper;

    private final DomainEventService eventService;

    private final ChannelRepository channelRepository;

    private final ChannelMemberRepository channelMemberRepository;

    private final UserRepository userRepository;

    public MessageService(MessageRepository messageRepository,
            MessageAuthorizationService authService,
            MessageMapper messageMapper, DomainEventService eventService, ChannelRepository channelRepository,
            ChannelMemberRepository channelMemberRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.authService = authService;
        this.messageMapper = messageMapper;
        this.eventService = eventService;
        this.channelRepository = channelRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.userRepository = userRepository;
    }

    public void create(UUID channelId, String content, CurrentUser user) {
        var auth = authService.authorizeMessageCreate(user, channelId);

        if (!auth.isAllowed()) {
            throw new AccessDeniedException();
        }

        var channel = channelRepository.findOne(channelId);

        var message = new Message();
        message.setChannelId(channelId);
        message.setSenderId(user.id());
        message.setContent(content);
        message.setSequenceNumber(channelRepository.getNextMessageSeq(channelId));
        message.setCreatedAt(Instant.now());

        messageRepository.save(message);

        var update = new ChannelMember.Update();
        update.setLastReadMessageSeq(message.getSequenceNumber());

        channelMemberRepository.update(update, Criteria.where("channel.id").is(channelId).and("userId").is(user.id()));

        message.setSender(userRepository.findOne(user.id()));

        eventService.publish(
                new MessageCreatedEvent(channel.getMemberIds(), messageMapper.toEventPayload(message)));
    }

    public List<MessageDto> findAll(UUID channelId, int limit, CurrentUser currentUser) {
        var auth = authService.authorizeMessageRead(currentUser, channelId);

        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);

        var messages = messageRepository.findAllWithSender(limit, fragment.getCriteria());
        return messageMapper.toDtoList(messages);
    }

    public void updateContent(UUID messageId, String content,
            CurrentUser user) {
        var auth = authService.authorizeMessageUpdateContent(user, messageId);

        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);
        var message = messageRepository.updateContent(fragment.getCriteria(), content);

        if (message == null) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findOne(message.getChannelId());

        eventService
                .publish(new MessageEditedEvent(channel.getMemberIds(),
                        new MessageEditedEvent.Payload(messageId, content)));
    }

    public void delete(UUID messageId, CurrentUser user) {
        var auth = authService.authorizeMessageDelete(user, messageId);

        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);
        var d = messageRepository.deleteOne(fragment.getCriteria());

        if (d == null) {
            throw new EntityNotFoundException();
        }

        var channel = channelRepository.findOne(d.getChannelId());
        eventService
                .publish(new MessageDeletedEvent(channel.getMemberIds(),
                        new MessageDeletedEvent.Payload(messageId)));
    }
}
