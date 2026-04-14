package net.chess_platform.common.permission.predicate;

import org.springframework.data.mongodb.core.query.Criteria;

public interface QueryCondition {

    Criteria toMongoCriteria();

    JPASpecification toJPASpecification();

    QueryCondition simplify();

    QueryCondition optimize();
}
