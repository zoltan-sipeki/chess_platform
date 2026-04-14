package net.chess_platform.common.permission;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class JPAQueryFragment<T> implements QueryFragment<T> {

    public static class False<T> extends JPAQueryFragment<T> {

        public False() {
            super((root, query, cb) -> cb.disjunction());
        }
    }

    public static class True<T> extends JPAQueryFragment<T> {

        public True() {
            super((root, query, cb) -> cb.conjunction());
        }
    }

    private Specification<T> specification;

    public JPAQueryFragment(Specification<T> specification) {
        this.specification = specification;
    }

    public Specification<T> getSpecification() {
        return specification;
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return specification.toPredicate(root, query, cb);
    }
}
