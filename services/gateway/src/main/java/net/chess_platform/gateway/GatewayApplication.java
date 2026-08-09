package net.chess_platform.gateway;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.web.servlet.function.RouterFunctions.route;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import net.chess_platform.common.security.EnableCommonSecurity;
import net.chess_platform.gateway.handler.PrivacyHandler;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCommonSecurity
public class GatewayApplication {

	private final PrivacyHandler privacyHandler;

	public GatewayApplication(PrivacyHandler privacyHandler) {
		this.privacyHandler = privacyHandler;
	}

	@Bean
	public Filter requestLoggingFilter() {
		return (request, response, chain) -> {
			HttpServletRequest req = (HttpServletRequest) request;
			System.out.println("REQUEST URI: " + req.getRequestURI() + " METHOD: " + req.getMethod());
			chain.doFilter(request, response);
		};
	}

	@Bean
	RouterFunction<ServerResponse> routes() {
		var chatRoutes = route(
				path("/api/channels/**")
						.or(path("/api/users/{id}/friends/**"))
						.or(path("/api/users/{id}/contacts/**"))
						.or(path("/api/notifications/**"))
						.or(path("/api/friend-requests/**"))
						.or(path("/api/relationships/**")),
				http())
				.filter(lb("chat-service"));

		var matchRoutes = route(
				path("/api/matches/**")
						.or(path("/api/stats/**"))
						.or(path("/api/leaderboard/**")),
				http())
				.filter(lb("match-service"));

		var matchmakingRoutes = route(path("/api/matchmaking/**"), http()).filter(lb("matchmaking-api-service"));

		var relayRoutes = route(path("/api/users/{id}/preferred-presence/**"), http()).filter(lb("relay-service"));

		var userRoutes = route(path("/api/users/**").or(path("/api/avatars/**")), http()).filter(lb("user-service"));

		var privacy = route().GET("/api/privacy/**", privacyHandler).build();

		var chatPrivacy = route().PATCH("/api/privacy/social/**", http())
				.before(rewritePath("/api/privacy/social", "/api/privacy"))
				.filter(lb("chat-service")).build();

		var matchPrivacy = route().PATCH("/api/privacy/match/**", http())
				.before(rewritePath("/api/privacy/match", "/api/privacy"))
				.filter(lb("match-service")).build();

		return chatRoutes.and(matchRoutes).and(matchmakingRoutes).and(relayRoutes).and(userRoutes).and(privacy)
				.and(chatPrivacy)
				.and(matchPrivacy);
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
