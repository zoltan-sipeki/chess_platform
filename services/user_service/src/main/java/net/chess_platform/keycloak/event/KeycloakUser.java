package net.chess_platform.keycloak.event;

public class KeycloakUser {

    public static class Builder {

        private KeycloakUser instance = new KeycloakUser();

        public Builder(String id) {
            instance.id = id;
        }

        public Builder username(String username) {
            instance.username = username;
            return this;
        }

        public Builder email(String email) {
            instance.email = email;
            return this;
        }

        public KeycloakUser build() {
            return instance;
        }
    }

    private String id;

    private String username;

    private String email;

    public KeycloakUser() {
    }

    public KeycloakUser(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
