package net.chess_platform.match_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.OngoingMatch;

@Repository
public interface OngoingMatchRepository
        extends JpaRepository<OngoingMatch, UUID>, JpaSpecificationExecutor<OngoingMatch> {

    default Optional<OngoingMatch> findOne(Authorization auth) {
        JPAQueryFragment<OngoingMatch> fragment = auth.getQueryFragment(OngoingMatch.class);
        return findOne(fragment.getSpecification());
    }

    default List<OngoingMatch> saveAllAndFlush(List<OngoingMatch> match, Authorization auth) {
        if (!auth.canCreate(match)) {
            return null;
        }

        return saveAllAndFlush(match);
    }

    @Modifying
    @Query("DELETE FROM OngoingMatch m WHERE m.matchId = :matchId")
    public int deleteByMatchId(long matchId);
}
