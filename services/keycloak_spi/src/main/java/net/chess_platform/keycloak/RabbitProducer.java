package net.chess_platform.keycloak;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitProducer {

    private final ConcurrentLinkedQueue<Channel> channelPool = new ConcurrentLinkedQueue<>();

    private final ObjectMapper objectMapper;

    private volatile Connection connection;

    public RabbitProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void createConnection(String host, int port) throws IOException, TimeoutException {
        var factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);

        this.connection = factory.newConnection();
    }

    public void declareExchange(String name, BuiltinExchangeType type, boolean durable, boolean autoDelete)
            throws IOException {
        var channel = getChannel();

        try {
            channel.exchangeDeclare(name, type, durable, autoDelete, null);
        } finally {
            channelPool.add(channel);
        }
    }

    public void publish(String exchange, String routingKey, Object message) throws IOException {
        var channel = getChannel();

        var builder = new BasicProperties.Builder();
        builder.contentType("application/json");
        builder.deliveryMode(1);
        builder.headers(Map.of("__TypeId__", message.getClass().getName()));

        var messageProperties = builder.build();
        try {

            channel.basicPublish(exchange, routingKey, messageProperties,
                    objectMapper.writeValueAsString(message).getBytes());
        } finally {
            channelPool.add(channel);
        }

    }

    public void close() throws IOException {
        connection.close();
    }

    private Channel getChannel() throws IOException {
        var channel = channelPool.poll();
        if (channel != null) {
            return channel;
        }

        return connection.createChannel();
    }
}
