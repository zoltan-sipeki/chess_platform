package net.chess_platform.match_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    @EntityGraph(attributePaths = { "matchDetails" })
    @Query("SELECT m FROM Match m INNER JOIN MatchDetail md ON m.id = md.match.id WHERE md.user.id = :userId")
    public Page<Match> findByUserId(UUID userId, Pageable pageable);
}
