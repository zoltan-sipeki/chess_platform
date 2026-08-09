package net.chess_platform.match_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchStat;

@Repository
public interface MatchStatRepository extends JpaRepository<MatchStat, UUID>, JpaSpecificationExecutor<MatchStat> {

    public Optional<MatchStat> findByPlayerIdAndMatchType(UUID userId, Match.Type matchType);

}
