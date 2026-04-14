package net.chess_platform.matchmaking_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.chess_platform.common.domain_events.EnableDomainEvents;

@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableDomainEvents
@EntityScan("net.chess_platform")
public class MatchMakingServiceApplication implements CommandLineRunner {
	@Override
	public void run(String... args) throws Exception {
	}

	public static void main(String[] args) {
		SpringApplication.run(MatchMakingServiceApplication.class, args);
	}
}
