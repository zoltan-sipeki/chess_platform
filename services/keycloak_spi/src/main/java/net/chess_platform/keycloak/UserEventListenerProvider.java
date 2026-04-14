package net.chess_platform.keycloak;

import static net.chess_platform.keycloak.Constants.KC_EVENTS_ROUTING_KEY;
import static net.chess_platform.keycloak.Constants.KEYCLOAK_EVENTS_EXCHANGE;

import java.io.IOException;
import java.util.List;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;

public class UserEventListenerProvider implements EventListenerProvider {

    private final KeycloakSession session;

    private final RabbitProducer producer;

    public UserEventListenerProvider(KeycloakSession session, RabbitProducer producer) {
        this.session = session;
        this.producer = producer;
    }

    @Override
    public void onEvent(Event event) {
        if (event.getType() == EventType.REGISTER) {
            var realm = session.realms().getRealm(event.getRealmId());
            var user = session.users().getUserById(realm, event.getUserId());
            user.setAttribute("synced", List.of("false"));
            user.setAttribute("displayName", List.of(user.getUsername()));
            user.setEmailVerified(true);
            publishUser(event.getRealmId(), event.getUserId());
        }
    }

    private void publishUser(String realmId, String userId) {
        var realm = session.realms().getRealm(realmId);
        var userModel = session.users().getUserById(realm, userId);

        var message = KeycloakUserVerifiedMessage.fromUserModel(realm.getName(), userModel);

        try {
            producer.publish(KEYCLOAK_EVENTS_EXCHANGE, KC_EVENTS_ROUTING_KEY, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {

    }

    @Override
    public void close() {
    }

}
