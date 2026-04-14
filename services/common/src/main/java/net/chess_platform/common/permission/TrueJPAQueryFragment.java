package net.chess_platform.common.permission;

public class TrueJPAQueryFragment<T> extends JPAQueryFragment<T> {

    public TrueJPAQueryFragment() {
        super((root, query, cb) -> cb.conjunction());
    }
}
