package net.chess_platform.matchmaking_connection_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.chess_platform.common.security.EnableCommonSecurity;

@SpringBootApplication
@EnableCommonSecurity
public class MatchmakingConnectionServiceApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MatchmakingConnectionServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}

}
