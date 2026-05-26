package net.chess_platform.match_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.model.PrivacySetting.Resource;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySetting, UUID> {

    @Query("SELECT ps FROM PrivacySetting ps WHERE ps.player.id = :playerId AND ps.resource = :resource")
    PrivacySetting findByUserIdAndResource(UUID playerId, Resource resource);

    @Query("SELECT ps FROM PrivacySetting ps WHERE ps.player.id = :playerId")
    List<PrivacySetting> findByPlayerId(UUID playerId);

    @Transactional
    @Modifying
    @Query("UPDATE PrivacySetting ps SET ps.restriction = :restriction WHERE ps.player.id = :userId AND ps.resource = 'MATCH_STATS'")
    int updateMatchStats(UUID userId, PrivacySetting.Restriction restriction);

    @Transactional
    @Modifying
    @Query("UPDATE PrivacySetting ps SET ps.restriction = :restriction WHERE ps.player.id = :userId AND ps.resource = 'PLAYER_STATS'")
    int updatePlayerStats(UUID userId, PrivacySetting.Restriction restriction);

    @Transactional
    @Modifying
    @Query("UPDATE PrivacySetting ps SET ps.restriction = :restriction WHERE ps.player.id = :userId AND ps.resource = 'MATCH_HISTORY'")
    int updateMatchHistory(UUID userId, PrivacySetting.Restriction restriction);
}
