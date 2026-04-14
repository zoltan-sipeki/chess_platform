package net.chess_platform.common.security;

import java.util.Set;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;

public record CurrentUser(
        UUID id,
        Jwt jwt,
        Set<String> roles) {

    public boolean hasRole(String role) {
        return roles.contains("ROLE_" + role);
    }
}
