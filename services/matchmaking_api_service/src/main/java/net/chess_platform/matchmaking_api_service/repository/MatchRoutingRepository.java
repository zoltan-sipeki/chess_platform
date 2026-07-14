package net.chess_platform.matchmaking_api_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.matchmaking_api_service.model.MatchRouting;

@Repository
public interface MatchRoutingRepository extends JpaRepository<MatchRouting, UUID> {

    @Query("SELECT m FROM MatchRouting m WHERE m.playerId = :playerId AND (m.matchStatus = 'ACTIVE' OR m.matchStatus = 'PENDING' AND m.expiresAt > CURRENT_TIMESTAMP)")
    Optional<MatchRouting> findByPlayerId(UUID playerId);

}
