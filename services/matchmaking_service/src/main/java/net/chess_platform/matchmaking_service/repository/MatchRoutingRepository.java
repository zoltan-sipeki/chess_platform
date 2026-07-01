package net.chess_platform.matchmaking_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.matchmaking_service.model.MatchRouting;

@Repository
public interface MatchRoutingRepository extends JpaRepository<MatchRouting, UUID> {

    @Query("SELECT COUNT(m) > 0 FROM MatchRouting m WHERE m.playerId = :playerId AND (m.matchStatus = 'ACTIVE' OR m.expiresAt > CURRENT_TIMESTAMP AND m.matchStatus = 'PENDING')")
    boolean hasActiveMatch(UUID playerId);

    @Modifying
    @Query("DELETE FROM MatchRouting m WHERE m.playerId = :playerId and m.matchStatus = 'PENDING'")
    int deletePending(UUID playerId);

    @Modifying
    @Query("DELETE FROM MatchRouting m WHERE m.expiresAt < CURRENT_TIMESTAMP AND m.matchStatus = 'PENDING'")
    int cleanUpStaleData();

}
