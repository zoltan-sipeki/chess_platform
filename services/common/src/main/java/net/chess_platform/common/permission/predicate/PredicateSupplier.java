package net.chess_platform.common.permission.predicate;

@FunctionalInterface
public interface PredicateSupplier {

    public QueryCondition getAsPredicate();
}
