package net.chess_platform.chat_service.config;

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

    @Value("${rabbitmq-messaging.chat-service.events.exchange}")
    private String CHAT_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.user-service.events.exchange}")
    private String USER_EVENTS_EXCHANGE;

    @Value("${rabbitmq-messaging.routing-key.service}")
    private String SERVICE_ROUTING_KEY;

    @Value("${rabbitmq-messaging.queue.service}")
    private String SERVICE_QUEUE;

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate chatEventsRabbitTemplate(RabbitTemplateConfigurer configurer,
            ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        var template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        template.setExchange(CHAT_EVENTS_EXCHANGE);
        template.setMessageConverter(messageConverter);
        return template;
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
    public Queue eventQueue() {
        return QueueBuilder.durable(SERVICE_QUEUE).build();
    }

    @Bean
    public Exchange userEventsExchange() {
        return ExchangeBuilder.directExchange(USER_EVENTS_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding userEventsBinding(Queue eventQueue, Exchange userEventsExchange) {
        return new Binding(eventQueue.getName(), Binding.DestinationType.QUEUE, userEventsExchange.getName(),
                SERVICE_ROUTING_KEY, null);
    }

}
