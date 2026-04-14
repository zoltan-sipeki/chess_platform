package net.chess_platform.common.permission;

import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;

public class MongoQueryFragment<T> implements QueryFragment<T> {

    public static class False<T> extends MongoQueryFragment<T> {

        public False() {
            super(new Criteria().and("$expr").is(new Document("$eq", List.of(1, 0))));
        }
    }

    public static class True<T> extends MongoQueryFragment<T> {

        public True() {
            super(new Criteria().and("$expr").is(new Document("$eq", List.of(1, 1))));
        }
    }

    private Criteria criteria;

    public MongoQueryFragment(Criteria criteria) {
        this.criteria = criteria;
    }

    public Criteria getCriteria() {
        return criteria;
    }
}
