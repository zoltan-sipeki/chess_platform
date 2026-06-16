package net.chess_platform.relay_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.relay_service.model.RelayUser;
import net.chess_platform.relay_service.model.RelayUser.Presence;

@Repository
public interface RelayUserRepository extends JpaRepository<RelayUser, UUID> {

    @Transactional
    @Modifying
    @Query("UPDATE RelayUser u SET u.preferredPresence = :status WHERE u.id = :userId")
    public int updatePreferredPresence(UUID userId, Presence status);
}
