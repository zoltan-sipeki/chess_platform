package net.chess_platform.common.permission;

import org.springframework.data.jpa.domain.PredicateSpecification;

public class JPAQueryFragment<T> implements QueryFragment<T> {

    public static class False<T> extends JPAQueryFragment<T> {

        public False() {
            super((from, cb) -> cb.disjunction());
        }
    }

    public static class True<T> extends JPAQueryFragment<T> {

        public True() {
            super((from, cb) -> cb.conjunction());
        }
    }

    private PredicateSpecification<T> specification;

    public JPAQueryFragment(PredicateSpecification<T> specification) {
        this.specification = specification;
    }

    public PredicateSpecification<T> getSpecification() {
        return specification;
    }
}
