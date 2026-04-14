package net.chess_platform.chat_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import net.chess_platform.common.domain_events.EnableDomainEvents;
import net.chess_platform.common.security.EnableCommonSecurity;

@SpringBootApplication
@EnableDomainEvents
@EntityScan("net.chess_platform")
@EnableDiscoveryClient
@EnableCommonSecurity
@EnableJpaAuditing
public class ChatServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ChatServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
