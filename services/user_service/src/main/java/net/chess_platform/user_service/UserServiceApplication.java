package net.chess_platform.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.chess_platform.common.domain_events.EnableDomainEvents;
import net.chess_platform.common.security.EnableCommonSecurity;

@SpringBootApplication
@EnableDomainEvents
@EntityScan("net.chess_platform")
@EnableJpaAuditing
@EnableDiscoveryClient
@EnableCommonSecurity
@EnableScheduling
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
