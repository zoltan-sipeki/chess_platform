package net.chess_platform.relay_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.relay_service.model.RelayUser;
import net.chess_platform.relay_service.model.RelayUser.Activity;
import net.chess_platform.relay_service.model.RelayUser.Presence;

@Repository
public interface RelayUserRepository extends JpaRepository<RelayUser, UUID> {

    @Modifying
    @Query("UPDATE RelayUser u SET u.activity = null WHERE u.activity = 'LOOKING_FOR_MATCH' AND u.id = :userId")
    public int clearLookingForMatch(UUID userId);

    @Modifying
    @Query("UPDATE RelayUser u SET u.activity = null WHERE u.activity = 'IN_MATCH' AND u.id = :userId")
    public int clearInMatch(UUID userId);

    @Modifying
    @Query("UPDATE RelayUser u SET u.preferredPresence = :status WHERE u.id = :userId")
    public int updatePreferredPresence(UUID userId, Presence status);

    @Query("UPDATE RelayUser u SET u.activity = :activity WHERE u.id = :userId")
    public int updateActivity(UUID userId, Activity activity);

    @Modifying
    @Query("UPDATE RelayUser u SET u.presence = :presence WHERE u.id = :userId")
    @Transactional
    public int updatePresence(UUID userId, Presence presence);
}
