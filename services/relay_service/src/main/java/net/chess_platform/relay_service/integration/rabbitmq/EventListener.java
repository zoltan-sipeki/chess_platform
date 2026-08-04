package net.chess_platform.relay_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.ActivityChangedEvent;
import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;
import net.chess_platform.common.domain_events.service.DomainEventService;
import net.chess_platform.relay_service.exception.UserAlreadyExistsException;
import net.chess_platform.relay_service.integration.ChatServiceProxy;
import net.chess_platform.relay_service.model.RelayUser.Presence;
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
    public void process(@Payload BroadcastEvent<?> e) {
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

    @RabbitHandler
    public void process(@Payload ActivityChangedEvent e) {
        var d = e.getData();
        if (userService.getPreferredPresence(d.userId()) == Presence.OFFLINE) {
            return;
        }

        var contacts = chatService.getContacts(d.userId());
        for (var recipient : contacts) {
            var payload = new EventPayload(e.getType(), d);
            connections.sendMessage(recipient,
                    new ServerMessage(ServerMessage.Type.EVENT, payload));
        }
    }

    private void broadcastEvent(BroadcastEvent<?> e) {
        for (var recipient : e.getRecipients()) {
            var payload = new EventPayload(e.getType(), e.getData());
            connections.sendMessage(recipient,
                    new ServerMessage(ServerMessage.Type.EVENT, payload));
        }
    }
}
