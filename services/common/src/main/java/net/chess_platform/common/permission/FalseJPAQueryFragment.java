package net.chess_platform.common.permission;

public class FalseJPAQueryFragment<T> extends JPAQueryFragment<T> {

    public FalseJPAQueryFragment() {
        super((root, query, cb) -> cb.disjunction());
    }
}
