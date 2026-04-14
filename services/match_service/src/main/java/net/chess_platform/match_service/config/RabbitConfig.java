package net.chess_platform.match_service.config;

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

    @Value("${rabbitmq-messaging.routing-key.chess-service}")
    private String CHESS_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.user-service}")
    private String USER_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.chess-service.events.exchange}")
    private String CHESS_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.user-service.events.exchange}")
    private String USER_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.queue.service}")
    private String SERVICE_QUEUE;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate chessEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setExchange(CHESS_EVENTS_EXCHANGE);
        template.setRoutingKey(CHESS_SERVICE_ROUTING_KEY);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public RabbitTemplate userEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setExchange(USER_EVENTS_EXCHANGE);
        template.setRoutingKey(USER_SERVICE_ROUTING_KEY);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public Exchange chessEventsExchange() {
        return ExchangeBuilder.directExchange(CHESS_EVENTS_EXCHANGE).durable(true).build();
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
    public Binding chessEventsBinding(Queue eventQueue,
            @Qualifier("chessEventsExchange") Exchange chessEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE, chessEventsExchange.getName(),
                SERVICE_ROUTING_KEY, null);
    }

    @Bean
    public Binding userEventsBinding(Queue eventQueue,
            @Qualifier("userEventsExchange") Exchange userEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE, userEventsExchange.getName(),
                SERVICE_ROUTING_KEY, null);
    }
}
