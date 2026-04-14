package net.chess_platform.common.permission.predicate;

import java.util.Arrays;
import java.util.Set;

import org.springframework.data.mongodb.core.query.Criteria;

public class Nin implements QueryCondition {

    private String field;

    private Set<Object> value;

    public Nin(String field, Set<Object> value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb.not(cb.in(path.get(field)).value(value));
    }

    @Override
    public Criteria toMongoCriteria() {
        return Criteria.where(field).in(value);
    }

    @Override
    public QueryCondition simplify() {
        return this;
    }

    public String getField() {
        return field;
    }

    public Set<Object> getValue() {
        return value;
    }

    @Override
    public QueryCondition optimize() {
        return this;
    }

    @Override
    public String toString() {
        return field + " NOT IN " + Arrays.asList(value);
    }
}
