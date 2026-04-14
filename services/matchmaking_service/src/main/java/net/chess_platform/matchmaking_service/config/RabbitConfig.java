package net.chess_platform.matchmaking_service.config;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Base64UrlNamingStrategy;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${spring.application.name}")
    private String APP_NAME;

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.matchmaking-service.events.exchange}")
    private String MATCHMAKING_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.user-service.events.exchange}")
    private String USER_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.matchmaking-service.reply.exchange}")
    private String MATCHMAKING_REPLY_EXCHANGE;

    @Value("${rabbitmq-messaging.routing-key.relay-service}")
    private String RELAY_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.queue.service}")
    private String SERVICE_QUEUE;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate matchmakingEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setExchange(MATCHMAKING_EVENTS_EXCHANGE);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public RabbitTemplate userEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setExchange(USER_EVENTS_EXCHANGE);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Queue replyQueue() {
        return new AnonymousQueue(new Base64UrlNamingStrategy(APP_NAME));
    }

    @Bean
    public Queue eventQueue() {
        return QueueBuilder.durable(SERVICE_QUEUE).build();
    }

    @Bean
    public Exchange matchmakingReplyExchange() {
        return ExchangeBuilder.directExchange(MATCHMAKING_REPLY_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange userEventsExchange() {
        return ExchangeBuilder.directExchange(USER_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding matchmakingReplyBinding(@Qualifier("replyQueue") Queue replyQueue,
            @Qualifier("matchmakingReplyExchange") Exchange matchmakingReplyExchange) {
        return new Binding(replyQueue.getName(), Binding.DestinationType.QUEUE,
                matchmakingReplyExchange.getName(), SERVICE_ROUTING_KEY, null);
    }

    @Bean
    public Binding userEventsBinding(@Qualifier("eventQueue") Queue eventQueue,
            @Qualifier("userEventsExchange") Exchange userEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE,
                userEventsExchange.getName(), SERVICE_ROUTING_KEY, null);
    }
}
