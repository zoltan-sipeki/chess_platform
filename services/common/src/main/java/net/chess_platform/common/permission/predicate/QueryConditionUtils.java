package net.chess_platform.common.permission.predicate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QueryConditionUtils {

    public static QueryCondition all() {
        return new True();
    }

    public static QueryCondition none() {
        return new False();
    }

    public static QueryCondition and(PredicateSupplier... predicates) {
        return new And(Arrays.stream(predicates).map(PredicateSupplier::getAsPredicate).toList());
    }

    public static QueryCondition and(QueryCondition... predicates) {
        return new And(Arrays.asList(predicates));
    }

    public static QueryCondition or(PredicateSupplier... predicates) {
        return new Or(Arrays.stream(predicates).map(PredicateSupplier::getAsPredicate).toList());
    }

    public static QueryCondition or(QueryCondition... predicates) {
        return new Or(Arrays.asList(predicates));
    }

    public static QueryCondition eq(String field, Object value) {
        return new Eq(field, value);
    }

    public static QueryCondition neq(String field, Object value) {
        return new Neq(field, value);
    }

    public static QueryCondition in(String field, Object[] value) {
        return new In(field, Set.of(value));
    }

    public static QueryCondition in(String field, List<?> value) {
        return new In(field, new HashSet<>(value));
    }

    public static QueryCondition nin(String field, Object[] value) {
        return new Nin(field, Set.of(value));
    }
    
    public static QueryCondition nin(String field, List<?> value) {
        return new Nin(field, new HashSet<>(value));
    }

    public static QueryCondition gt(String field, Object value) {
        return new Gt(field, value);
    }

    public static QueryCondition gte(String field, Object value) {
        return new Gte(field, value);
    }

    public static QueryCondition lt(String field, Object value) {
        return new Lt(field, value);
    }

    public static QueryCondition lte(String field, Object value) {
        return new Lte(field, value);
    }
}
