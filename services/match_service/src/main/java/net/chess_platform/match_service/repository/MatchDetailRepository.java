package net.chess_platform.match_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.JPAQueryFragment;
import net.chess_platform.match_service.model.MatchDetail;

@Repository
public interface MatchDetailRepository extends JpaRepository<MatchDetail, UUID>, JpaSpecificationExecutor<MatchDetail> {

    default Page<MatchDetail> findAll(Authorization auth, Pageable pageable) {
        JPAQueryFragment<MatchDetail> fragment = auth.getQueryFragment(MatchDetail.class);
        return findAll(fragment.getSpecification(), pageable);
    }

    @EntityGraph(attributePaths = { "match" })
    Page<MatchDetail> findAll(Specification<MatchDetail> spec, Pageable pageable);
}
