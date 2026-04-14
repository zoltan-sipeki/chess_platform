package net.chess_platform.match_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.MatchUser;

@Repository
public interface MatchUserRepository extends JpaRepository<MatchUser, UUID> {

}
