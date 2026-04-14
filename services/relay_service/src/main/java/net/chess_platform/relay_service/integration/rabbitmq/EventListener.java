package net.chess_platform.relay_service.integration.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import net.chess_platform.common.domain_events.broker.chat.SocialEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundBroadcastEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.relay_service.service.UserEventService;
import net.chess_platform.relay_service.ws.WSConnections;
import net.chess_platform.relay_service.ws.message.EventPayload;
import net.chess_platform.relay_service.ws.message.ServerMessage;

@Component
@RabbitListener(queues = { "#{eventQueue.name}", "#{eventFanoutQueue.name}" }, messageConverter = "messageConverter")
public class EventListener {

    private WSConnections connections;

    private UserEventService userEventService;

    public EventListener(WSConnections connections, UserEventService userEventService) {
        this.connections = connections;
        this.userEventService = userEventService;
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
        userEventService.process(e);
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
