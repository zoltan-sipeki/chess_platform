package net.chess_platform.gateway;

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
import net.chess_platform.gateway.handler.DashboardHandler;
import net.chess_platform.gateway.handler.ProfileHandler;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCommonSecurity
public class GatewayApplication {

	private final ProfileHandler profileHandler;

	private final DashboardHandler dashboardHandler;

	public GatewayApplication(ProfileHandler profileHandler, DashboardHandler dashboardHandler) {
		this.profileHandler = profileHandler;
		this.dashboardHandler = dashboardHandler;
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
	public RouterFunction<ServerResponse> chatServiceRoutes() {
		return route(
				path("/api/channels/**").or(
						path("/api/friends/**").or(
								path("/api/notifications/**")).or(
										path("/api/friendRequests/**"))),
				http())
				.filter(lb("chat-service"));
	}

	@Bean
	public RouterFunction<ServerResponse> matchServiceRoutes() {
		return route(path("/api/matches/**").or(path("/api/ratings/**")), http()).filter(lb("match-service"));
	}

	@Bean
	public RouterFunction<ServerResponse> matchmakingServiceRoutes() {
		return route(path("/api/queues/**"), http()).filter(lb("matchmaking-service"));
	}

	@Bean
	public RouterFunction<ServerResponse> userServiceRoutes() {
		return route(path("/api/users/**"), http()).filter(lb("user-service"));
	}

	@Bean
	public RouterFunction<ServerResponse> profiles() {
		return route().GET("/api/profiles/{userId}", profileHandler).build();
	}

	@Bean
	public RouterFunction<ServerResponse> dashboard() {
		return route().GET("/api/dashboard", dashboardHandler).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
