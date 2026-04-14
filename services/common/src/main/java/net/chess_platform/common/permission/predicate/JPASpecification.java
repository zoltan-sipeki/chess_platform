package net.chess_platform.common.permission.predicate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

@FunctionalInterface
public interface JPASpecification {

    Predicate toPredicate(Path<?> path, CriteriaQuery<?> query, CriteriaBuilder cb);
}
