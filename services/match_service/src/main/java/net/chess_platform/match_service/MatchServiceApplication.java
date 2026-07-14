package net.chess_platform.match_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import net.chess_platform.common.domain_events.EnableDomainEvents;
import net.chess_platform.common.security.EnableCommonSecurity;
import tools.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableJpaAuditing
@EnableDomainEvents
@EntityScan("net.chess_platform")
@EnableCommonSecurity
public class MatchServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MatchServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

}
