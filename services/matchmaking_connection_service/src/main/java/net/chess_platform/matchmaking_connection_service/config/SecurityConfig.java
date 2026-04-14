package net.chess_platform.matchmaking_connection_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(
                                                sessionManagement -> sessionManagement
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer(
                                                oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                                jwtAuthenticationConverter)))
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll());

                return http.build();
        }

        @Bean
        public JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder decoder) {
                return new JwtAuthenticationProvider(decoder);
        }
}
