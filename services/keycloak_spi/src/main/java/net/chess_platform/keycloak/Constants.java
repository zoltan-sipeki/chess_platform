package net.chess_platform.keycloak;

public class Constants {

    public static final String KEYCLOAK_EVENTS_EXCHANGE = "keycloak-events-exchange";

    public static final String KC_EVENTS_ROUTING_KEY;
    static {
        var env = System.getenv("KC_EVENTS_ROUTING_KEY");
        KC_EVENTS_ROUTING_KEY = env == null ? "" : env;
    }

    public static final String KC_RABBITMQ_HOST;
    static {
        var env = System.getenv("KC_RABBITMQ_HOST");
        KC_RABBITMQ_HOST = env == null ? "" : env;
    }

    public static final int KC_RABBITMQ_PORT;
    static {
        var env = System.getenv("KC_RABBITMQ_PORT");
        int port = -1;
        try {
            port = Integer.parseInt(env);
        } catch (NumberFormatException e) {

        }
        KC_RABBITMQ_PORT = port;
    }
}
