package net.chess_platform.chat_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Friend;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class FriendRepository {

    private MongoOperations mongoTemplate;

    public FriendRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Friend> findAll(Authorization auth) {
        MongoQueryFragment<Friend> fragment = auth.getQueryFragment(Friend.class);

        var a = Aggregation.newAggregation(
                Aggregation.match(fragment.getCriteria()),
                Aggregation.lookup("user", "friendId", "_id", "friend"));

        return mongoTemplate.aggregate(a, Friend.class, Friend.class).getMappedResults();
    }

    public long delete(Authorization auth) {
        MongoQueryFragment<Friend> fragment = auth.getQueryFragment(Friend.class);
        return mongoTemplate.remove(Friend.class).matching(fragment.getCriteria()).all().getDeletedCount();
    }

    public boolean areFriends(UUID userId, UUID friendId) {
        return mongoTemplate.query(Friend.class)
                .matching(Criteria.where("userId").is(userId).and("friendId").is(friendId))
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
