package net.chess_platform.keycloak;

import static net.chess_platform.keycloak.Constants.KC_RABBITMQ_HOST;
import static net.chess_platform.keycloak.Constants.KC_RABBITMQ_PORT;
import static net.chess_platform.keycloak.Constants.KEYCLOAK_EVENTS_EXCHANGE;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.rabbitmq.client.BuiltinExchangeType;

public class UserEventListenerProdiverFactory implements EventListenerProviderFactory {

    private static final String PROVIDER_ID = "user-event-listener";

    private final RabbitProducer producer;

    private final ObjectMapper objectMapper;

    public UserEventListenerProdiverFactory() {
        objectMapper = new ObjectMapper();
        objectMapper.setDefaultPropertyInclusion(Include.NON_NULL);
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), DefaultTyping.NON_FINAL,
                As.PROPERTY);
        producer = new RabbitProducer(objectMapper);
    }

    @Override
    public void close() {
        try {
            producer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new UserEventListenerProvider(session, producer);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public void init(Scope scope) {
        try {
            producer.createConnection(KC_RABBITMQ_HOST, KC_RABBITMQ_PORT);
            producer.declareExchange(KEYCLOAK_EVENTS_EXCHANGE, BuiltinExchangeType.DIRECT, true, false);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

}
