package net.chess_platform.user_service.config;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.url}")
    private String KEYCLOAK_URL;

    @Value("${spring.application.name}")
    private String APP_NAME;

    @Bean
    public RestClient keyCloakRestClient(ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    var attributes = request.getAttributes();
                    clientRegistrationId("keycloak").accept(attributes);
                    return execution.execute(request, body);
                })
                .requestInterceptor(new OAuth2ClientHttpRequestInterceptor(authorizedClientManager))
                .baseUrl(KEYCLOAK_URL).build();
    }
}
