package net.chess_platform.chat_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Friend;

@Repository
public class FriendRepository {

    private MongoOperations mongoTemplate;

    public FriendRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Friend> findAll(UUID userId) {
        return findAll(Criteria.where("user").is(userId));
    }

    public List<Friend> findAll(Criteria criteria) {
        var a = Aggregation.newAggregation(
                Aggregation.match(criteria));

        return mongoTemplate.aggregate(a, Friend.class, Friend.class).getMappedResults();
    }

    public Page<Friend> findAll(Criteria criteria, Pageable pageable) {
        var a = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()));

        var result = mongoTemplate.aggregate(a, Friend.class, Friend.class).getMappedResults();
        var count = mongoTemplate.count(new Query(criteria), Friend.class);

        return new PageImpl<>(result, pageable, count);
    }

    public long deleteAll(Criteria criteria) {
        return mongoTemplate.remove(Friend.class).matching(criteria).all().getDeletedCount();
    }

    public boolean areFriends(UUID userId, UUID friendId) {
        return mongoTemplate.query(Friend.class)
                .matching(Criteria.where("user").is(userId).and("friend").is(friendId))
                .exists();
    }

    public long save(List<Friend> list) {
        return mongoTemplate
                .insert(Friend.class)
                .withBulkMode(BulkOperations.BulkMode.UNORDERED)
                .bulk(list)
                .getInsertedCount();
    }

}
