package net.chess_platform.match_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.LongestStreak;

@Repository
public interface LongestStreakRepository
        extends JpaRepository<LongestStreak, UUID>, JpaSpecificationExecutor<LongestStreak> {

    @Query("SELECT ls FROM LongestStreak ls WHERE ls.playerId = :playerId")
    List<LongestStreak> findAllByPlayerId(UUID playerId);
}
