package net.chess_platform.common.permission.predicate;

import java.util.Arrays;

import org.springframework.data.mongodb.core.query.Criteria;

public class Lt implements QueryCondition {

    private String field;

    private Object value;

    public Lt(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb.lt(path.get(field), (Number) value);
    }

    @Override
    public Criteria toMongoCriteria() {
        return Criteria.where(field).lt(value);
    }

    @Override
    public QueryCondition simplify() {
        return this;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public QueryCondition optimize() {
        return this;
    }

    @Override
    public String toString() {
        return field + " < " + Arrays.asList(value);
    }
}
