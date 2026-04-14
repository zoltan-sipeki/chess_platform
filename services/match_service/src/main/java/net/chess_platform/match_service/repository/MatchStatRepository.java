package net.chess_platform.match_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.Match;
import net.chess_platform.match_service.model.MatchStat;

@Repository
public interface MatchStatRepository extends JpaRepository<MatchStat, UUID>, JpaSpecificationExecutor<MatchStat> {

    default List<MatchStat> findAll(Authorization auth) {
        JPAQueryFragment<MatchStat> fragment = auth.getQueryFragment(MatchStat.class);
        return findAll(fragment.getSpecification());
    }

    public List<MatchStat> findByUserId(UUID userId);

    public Optional<MatchStat> findByUserIdAndMatchType(UUID userId, Match.Type matchType);

}
