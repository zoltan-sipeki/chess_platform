package net.chess_platform.matchmaking_connection_service.config;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
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

    @Value("${rabbitmq-messaging.matchmaking-service.reply.exchange}")
    private String MATCHMAKING_REPLY_EXCHANGE;

    @Value("${rabbitmq-messaging.routing-key.matchmaking-service}")
    private String MATCHMAKING_SERVICE_ROUTING_KEY;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate matchmakingServiceRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        var rabbitTemplate = new RabbitTemplate();
        configurer.configure(rabbitTemplate, connectionFactory);
        rabbitTemplate.setExchange(MATCHMAKING_REPLY_EXCHANGE);
        rabbitTemplate.setRoutingKey(MATCHMAKING_SERVICE_ROUTING_KEY);
        rabbitTemplate.setReplyTimeout(10000);
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Exchange matchmakingReplyExchange() {
        return ExchangeBuilder.directExchange(MATCHMAKING_REPLY_EXCHANGE).durable(true).build();
    }

}
