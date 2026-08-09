package net.chess_platform.match_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.Leaderboard;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID>, JpaSpecificationExecutor<Leaderboard> {

    @Query("SELECT l FROM Leaderboard l WHERE l.playerId = :userId")
    Optional<Leaderboard> findByPlayerId(UUID userId);
}
