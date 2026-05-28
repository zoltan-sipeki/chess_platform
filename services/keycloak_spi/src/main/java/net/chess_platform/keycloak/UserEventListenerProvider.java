package net.chess_platform.keycloak;

import static net.chess_platform.keycloak.Constants.KC_EVENTS_ROUTING_KEY;
import static net.chess_platform.keycloak.Constants.KEYCLOAK_EVENTS_EXCHANGE;

import java.io.IOException;
import java.util.List;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

import net.chess_platform.keycloak.event.KeycloakUser;
import net.chess_platform.keycloak.event.KeycloakUserUpdatedEvent;
import net.chess_platform.keycloak.event.KeycloakUserVerifiedEvent;

public class UserEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    private final RabbitProducer producer;

    public UserEventListenerProvider(KeycloakSession session, RabbitProducer producer) {
        this.session = session;
        this.producer = producer;
    }

    @Override
    public void onEvent(Event event) {
        switch (event.getType()) {
            case REGISTER -> {
                var realm = session.realms().getRealm(event.getRealmId());
                var user = session.users().getUserById(realm, event.getUserId());
                user.setAttribute("synced", List.of("false"));
                user.setAttribute("updated", List.of("false"));
                user.setEmailVerified(true);

                var kcu = new KeycloakUser(user.getId(), user.getUsername(), user.getEmail());

                try {
                    producer.publish(KEYCLOAK_EVENTS_EXCHANGE, KC_EVENTS_ROUTING_KEY,
                            new KeycloakUserVerifiedEvent(kcu));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case UPDATE_EMAIL -> {
                var realm = session.realms().getRealm(event.getRealmId());
                var user = session.users().getUserById(realm, event.getUserId());
                user.setAttribute("synced", List.of("false"));
                user.setAttribute("updated", List.of("true"));

                var kcu = new KeycloakUser.Builder(user.getId()).email(user.getEmail()).build();

                try {
                    producer.publish(KEYCLOAK_EVENTS_EXCHANGE, KC_EVENTS_ROUTING_KEY,
                            new KeycloakUserUpdatedEvent(kcu));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            default -> {
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {

    }

    @Override
    public void close() {
    }

}
