package net.chess_platform.match_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.LongestStreak;

@Repository
public interface LongestStreakRepository
        extends JpaRepository<LongestStreak, UUID>, JpaSpecificationExecutor<LongestStreak> {

    default List<LongestStreak> findAll(Authorization auth) {
        JPAQueryFragment<LongestStreak> fragment = auth.getQueryFragment(LongestStreak.class);
        return findAll(fragment.getSpecification());
    }
}
