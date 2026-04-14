package net.chess_platform.common.permission.predicate;

import org.springframework.data.mongodb.core.query.Criteria;

public class Neq implements QueryCondition {

    private String field;

    private Object value;

    public Neq(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb.notEqual(path.get(field), value);
    }

    @Override
    public Criteria toMongoCriteria() {
        return Criteria.where(field).is(value);
    }

    @Override
    public QueryCondition simplify() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Neq other = (Neq) obj;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
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
        return field + " = " + value;
    }
}
