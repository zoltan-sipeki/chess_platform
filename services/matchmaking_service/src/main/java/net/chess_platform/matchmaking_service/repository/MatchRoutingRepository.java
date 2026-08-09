package net.chess_platform.matchmaking_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.domain.PredicateSpecification;
import org.springframework.data.jpa.domain.UpdateSpecification.UpdateOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.matchmaking_service.model.MatchRouting;

@Repository
public interface MatchRoutingRepository
        extends JpaRepository<MatchRouting, UUID>, JpaSpecificationExecutor<MatchRouting> {

    @Query("SELECT m FROM MatchRouting m WHERE m.playerId = :playerId AND (m.matchStatus = 'ACTIVE' OR m.matchStatus = 'PENDING' AND m.expiresAt > CURRENT_TIMESTAMP)")
    Optional<MatchRouting> findByPlayerId(UUID playerId);

    @Query("SELECT COUNT(m) > 0 FROM MatchRouting m WHERE m.playerId = :playerId AND (m.matchStatus = 'ACTIVE' OR m.expiresAt > CURRENT_TIMESTAMP AND m.matchStatus = 'PENDING')")
    boolean hasActiveMatch(UUID playerId);

    @Modifying
    @Query("DELETE FROM MatchRouting m WHERE m.playerId = :playerId and m.matchStatus = 'PENDING'")
    int deletePending(UUID playerId);

    @Modifying
    @Query("DELETE FROM MatchRouting m WHERE m.expiresAt < CURRENT_TIMESTAMP AND m.matchStatus = 'PENDING'")
    int cleanUpStaleData();

    @Modifying
    @Query("DELETE FROM MatchRouting m WHERE m.matchId = :matchId")
    int delete(long matchId);

    default long update(long matchId, MatchRouting.Update update, PredicateSpecification<MatchRouting> spec) {
        if (update == null) {
            return 0;
        }
        UpdateOperation<MatchRouting> op = (root, u, cb) -> {
            var status = update.getMatchStatus();
            if (status != null) {
                u.set(root.get("matchStatus"), status);
            }
        };

        return update(op.where((root, cb) -> cb.equal(root.get("matchId"), matchId)).and(spec));
    }

}
