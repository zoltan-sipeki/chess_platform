package net.chess_platform.matchmaking_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.chess_platform.matchmaking_service.model.MatchmakingUser;

@Repository
public interface MatchmakingUserRepository extends JpaRepository<MatchmakingUser, UUID> {

}
