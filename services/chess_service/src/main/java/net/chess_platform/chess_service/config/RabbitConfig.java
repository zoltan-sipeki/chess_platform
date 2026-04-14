package net.chess_platform.chess_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${spring.application.name}")
    private String APP_NAME;

    @Value("${rabbitmq-messaging.chess-service.events.exchange}")
    private String CHESS_SERVICE_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.match-service}")
    private String MATCH_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.routing-key.relay-service}")
    private String RELAY_SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.queue.service}")
    private String SERVICE_QUEUE;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate chessEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setExchange(CHESS_SERVICE_EVENTS_EXCHANGE);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Exchange chessServiceEventsExchange() {
        return ExchangeBuilder.directExchange(CHESS_SERVICE_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue eventQueue() {
        return QueueBuilder.durable(SERVICE_QUEUE).build();
    }

    @Bean
    public Binding chessServiceEventsBinding(Queue eventQueue, Exchange chessServiceEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE, chessServiceEventsExchange.getName(),
                SERVICE_ROUTING_KEY, null);
    }

}
