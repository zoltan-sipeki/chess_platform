package net.chess_platform.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class WebConfig {

    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    var auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth == null) {
                        return execution.execute(request, body);
                    }

                    if (auth instanceof JwtAuthenticationToken a) {
                        request.getHeaders().setBearerAuth(a.getToken().getTokenValue());
                    }

                    return execution.execute(request, body);
                });
        // .requestInterceptor((request, body, execution) -> {
        // System.out.println("-------------------------------------");
        // System.out.println(request.getHeaders().get("Authorization").get(0));
        // System.out.println("-------------------------------------");
        // return execution.execute(request, body);
        // });
    }
}
