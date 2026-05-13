package net.chess_platform.match_service.repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID>, JpaSpecificationExecutor<Player> {

    default Optional<Player> findOne(Authorization auth) {
        JPAQueryFragment<Player> fragment = auth.getQueryFragment(Player.class);
        return findOne(fragment.getSpecification());
    }

    @Modifying
    @Query("UPDATE Player p SET p.rankedMmr = :rankedMmr, p.lastPlayedAt = :lastPlayedAt WHERE p.id = :playerId")
    public int updateRankedMmrByUserId(UUID playerId, int rankedMmr, OffsetDateTime lastPlayedAt);

    @Modifying
    @Query("UPDATE Player p SET p.unrankedMmr = :unrankedMmr, p.lastPlayedAt = :lastPlayedAt WHERE p.id = :playerId")
    public int updateUnrankedMmrByUserId(UUID playerId, int unrankedMmr, OffsetDateTime lastPlayed);

    @Modifying
    @Query("UPDATE Player p SET p.lastPlayedAt = :lastPlayed WHERE p.id = :playerId")
    public int updateLastPlayed(UUID playerId, OffsetDateTime lastPlayed);
}
