package net.chess_platform.match_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.PrivacySetting;
import net.chess_platform.match_service.model.PrivacySetting.Resource;

@Repository
public interface PrivacySettingRepository extends JpaRepository<PrivacySetting, UUID> {

    @Query("SELECT ps FROM PrivacySetting ps WHERE ps.user.id = :userId AND ps.resource = :resource")
    public PrivacySetting findByUserIdAndResource(UUID userId, Resource resource);
}
