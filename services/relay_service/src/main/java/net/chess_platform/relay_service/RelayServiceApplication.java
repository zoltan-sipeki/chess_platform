package net.chess_platform.relay_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.chess_platform.common.domain_events.EnableDomainEvents;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableDomainEvents
@EntityScan("net.chess_platform")
@EnableScheduling
public class RelayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RelayServiceApplication.class, args);
	}

}
