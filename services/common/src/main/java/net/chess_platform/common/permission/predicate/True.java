package net.chess_platform.common.permission.predicate;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

public class True implements QueryCondition {

    @Override
    public QueryCondition simplify() {
        return this;
    }

    @Override
    public JPASpecification toJPASpecification() {
        return (path, query, cb) -> cb.isTrue(cb.literal(true));
    }

    @Override
    public Criteria toMongoCriteria() {
        return new Criteria().and("$expr").is(new Document("$eq", List.of(1, 1)));
    }

    @Override
    public QueryCondition optimize() {
        return this;
    }

    @Override
    public String toString() {
        return "True";
    }
}
