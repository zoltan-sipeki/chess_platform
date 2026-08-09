package net.chess_platform.match_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.match_service.model.MatchResult;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResult, UUID>, JpaSpecificationExecutor<MatchResult> {
    
    @EntityGraph(attributePaths = { "match" })
    Page<MatchResult> findAll(Specification<MatchResult> spec, Pageable pageable);
}
