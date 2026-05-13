package net.chess_platform.match_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.Leaderboard;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, UUID>, JpaSpecificationExecutor<Leaderboard> {

    default Page<Leaderboard> findAll(Authorization auth, Pageable pageable) {
        JPAQueryFragment<Leaderboard> fragment = auth.getQueryFragment(Leaderboard.class);
        return findAll(fragment.getSpecification(), pageable);
    }

    default Optional<Leaderboard> findOne(Authorization auth) {
        JPAQueryFragment<Leaderboard> fragment = auth.getQueryFragment(Leaderboard.class);
        return findOne(fragment.getSpecification());
    }
}
