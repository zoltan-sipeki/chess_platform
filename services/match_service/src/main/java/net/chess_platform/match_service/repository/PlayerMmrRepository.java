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
import net.chess_platform.match_service.model.PlayerMmr;

@Repository
public interface PlayerMmrRepository extends JpaRepository<PlayerMmr, UUID>, JpaSpecificationExecutor<PlayerMmr> {

    default Optional<PlayerMmr> findOne(Authorization auth) {
        JPAQueryFragment<PlayerMmr> fragment = auth.getQueryFragment(PlayerMmr.class);
        return findOne(fragment.getSpecification());
    }

    @Modifying
    @Query("UPDATE PlayerMmr m SET m.rankedMmr = :rankedMmr, m.lastPlayed = :lastPlayed WHERE m.id = :userId")
    public int updateRankedMmrByUserId(UUID userId, int rankedMmr, OffsetDateTime lastPlayed);

    @Modifying
    @Query("UPDATE PlayerMmr m SET m.unrankedMmr = :unrankedMmr, m.lastPlayed = :lastPlayed WHERE m.id = :userId")
    public int updateUnrankedMmrByUserId(UUID userId, int unrankedMmr, OffsetDateTime lastPlayed);

    @Modifying
    @Query("UPDATE PlayerMmr m SET m.lastPlayed = :lastPlayed WHERE m.id = :userId")
    public int updateLastPlayed(UUID userId, OffsetDateTime lastPlayed);
}
