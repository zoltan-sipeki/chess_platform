package net.chess_platform.relay_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chat.SocialEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundBroadcastEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.relay_service.exception.UserAlreadyExistsException;
import net.chess_platform.relay_service.integration.ChatServiceProxy;
import net.chess_platform.relay_service.service.RelayUserService;
import net.chess_platform.relay_service.ws.WSConnections;
import net.chess_platform.relay_service.ws.message.EventPayload;
import net.chess_platform.relay_service.ws.message.ServerMessage;

@Component
@RabbitListener(queues = { "#{eventQueue.name}", "#{eventFanoutQueue.name}" }, messageConverter = "messageConverter")
public class EventListener {

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    private final WSConnections connections;

    private final RelayUserService userService;

    private final DomainEventService eventService;

    private final ChatServiceProxy chatService;

    public EventListener(WSConnections connections, RelayUserService userService, DomainEventService eventService,
            ChatServiceProxy chatService) {
        this.connections = connections;
        this.userService = userService;
        this.eventService = eventService;
        this.chatService = chatService;
    }

    @RabbitHandler
    public void process(@Payload MatchFoundEvent e) {
        var payload = new EventPayload(e.getType(), e.getData());
        connections.sendMessage(e.getRecipient(),
                new ServerMessage(ServerMessage.Type.EVENT, payload));

    }

    @RabbitHandler
    public void process(@Payload MatchFoundBroadcastEvent e) {
        var recipients = e.getRecipients();
        var tokens = e.getData().matchmakingTokens();
        for (var recipient : recipients) {
            var payload = new EventPayload(e.getType(), tokens.get(recipient));
            connections.sendMessage(recipient,
                    new ServerMessage(ServerMessage.Type.EVENT, payload));
        }
    }

    @RabbitHandler
    public void process(@Payload SocialEvent<?> e) {
        broadcastEvent(e);
    }

    @RabbitHandler
    public void process(@Payload UserCreatedEvent e) {
        try {
            userService.process(e);
            eventService.ack(e, SERVICE_NAME);
        } catch (UserAlreadyExistsException ex) {
            eventService.ack(e, SERVICE_NAME);
        }
    }

    @RabbitHandler
    public void process(@Payload UserUpdatedEvent e) {
        var contacts = chatService.getContacts(e.getData().id());
        for (var recipient : contacts) {
            var payload = new EventPayload(e.getType(), e.getData());
            connections.sendMessage(recipient,
                    new ServerMessage(ServerMessage.Type.EVENT, payload));
        }
    }

    // @RabbitHandler
    // public void process(@Payload MessageCreatedEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload MessageEditedEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload MessageDeletedEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload GroupChannelCreatedEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload GroupChannelMemberJoinedEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload GroupChannelMemberLeftEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload NotificationEvent e) {
    // broadcastEvent(e);
    // }

    // @RabbitHandler
    // public void process(@Payload UnfriendEvent e) {
    // broadcastEvent(e);
    // }

    private void broadcastEvent(SocialEvent<?> e) {
        for (var recipient : e.getRecipients()) {
            var payload = new EventPayload(e.getType(), e.getData());
            connections.sendMessage(recipient,
                    new ServerMessage(ServerMessage.Type.EVENT, payload));
        }
    }
}
