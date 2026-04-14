package net.chess_platform.relay_service.config;

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

    @Value("${rabbitmq-messaging.chess-service.events.exchange}")
    private String CHESS_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.chat-service.events.exchange}")
    private String CHAT_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.matchmaking-service.events.exchange}")
    private String MATCHMAKING_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.user-service.events.exchange}")
    private String USER_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.relay-service.events.exchange}")
    private String RELAY_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.relay-service.events.fanout-exchange}")
    private String RELAY_FANOUT_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.service-fanout}")
    private String SERVICE_FANOUT_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.chat-service}")
    private String CHAT_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.matchmaking-service}")
    private String MATCHMAKING_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.chess-service}")
    private String CHESS_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.queue.service}")
    private String SERVICE_QUEUE;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate userEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setExchange(USER_EVENTS_EXCHANGE);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public RabbitTemplate relayEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setExchange(RELAY_EVENTS_EXCHANGE);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Exchange relayEventsExchange() {
        return ExchangeBuilder.directExchange(RELAY_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange chessEventsExchange() {
        return ExchangeBuilder.directExchange(CHESS_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange matchmakingEventsExchange() {
        return ExchangeBuilder.directExchange(MATCHMAKING_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange chatEventsExchange() {
        return ExchangeBuilder.directExchange(CHAT_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Exchange userEventsExchange() {
        return ExchangeBuilder.directExchange(USER_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue eventQueue() {
        return QueueBuilder.durable(SERVICE_QUEUE).build();
    }

    @Bean
    public Exchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange(RELAY_FANOUT_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue eventFanoutQueue() {
        return new AnonymousQueue(new Base64UrlNamingStrategy(APP_NAME + "-"));
    }

    @Bean
    public Binding fanoutExchangeBinding(@Qualifier("eventFanoutQueue") Queue eventFanoutQueue,
            @Qualifier("fanoutExchange") Exchange fanoutExchange) {
        return new Binding(eventFanoutQueue.getName(), Binding.DestinationType.QUEUE, fanoutExchange.getName(),
                "", null);
    }

    @Bean
    public Binding relayEventsBinding(@Qualifier("fanoutExchange") Exchange fanoutExchange,
            @Qualifier("relayEventsExchange") Exchange relayEventsExchange) {
        return new Binding(fanoutExchange.getName(), Binding.DestinationType.EXCHANGE,
                relayEventsExchange.getName(),
                SERVICE_FANOUT_ROUTING_KEY, null);
    }

    @Bean
    public Binding matchmakingEventsBinding(@Qualifier("fanoutExchange") Exchange fanoutExchange,
            @Qualifier("matchmakingEventsExchange") Exchange matchmakingEventsExchange) {
        return new Binding(fanoutExchange.getName(), Binding.DestinationType.EXCHANGE,
                matchmakingEventsExchange.getName(),
                SERVICE_FANOUT_ROUTING_KEY, null);
    }

    @Bean
    public Binding chatEventsBinding(@Qualifier("fanoutExchange") Exchange fanoutExchange,
            @Qualifier("chatEventsExchange") Exchange chatEventsExchange) {
        return new Binding(fanoutExchange.getName(), Binding.DestinationType.EXCHANGE,
                chatEventsExchange.getName(),
                SERVICE_FANOUT_ROUTING_KEY, null);
    }

    @Bean
    public Binding userEventsBinding(@Qualifier("eventQueue") Queue eventQueue,
            @Qualifier("userEventsExchange") Exchange userEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE, userEventsExchange.getName(),
                SERVICE_ROUTING_KEY, null);
    }
}
