package net.chess_platform.chat_service.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.NotificationMetadata;

@Repository
public class NotificationRepository {

    private final MongoOperations mongoTemplate;

    public NotificationRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Notification> findAll(Criteria criteria, Long before, long limit) {
        if (before != null) {
            criteria = criteria.and("sequenceNumber").lt(before);
        }

        var a = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.by(Direction.DESC, "sequenceNumber")),
                Aggregation.limit(limit));

        return mongoTemplate.aggregate(a, Notification.class, Notification.class).getMappedResults();

    }

    public long countUnread(Criteria criteria, long lastReadSeq) {
        return mongoTemplate.count(
                new Query(Criteria.where("sequenceNumber").gt(lastReadSeq)
                        .andOperator(criteria)),
                Notification.class);
    }

    public long deleteOne(Criteria criteria) {
        return mongoTemplate
                .remove(Notification.class)
                .matching(criteria)
                .one()
                .getDeletedCount();
    }

    public long deleteAll(Criteria criteria) {
        return mongoTemplate
                .remove(Notification.class)
                .matching(criteria)
                .all()
                .getDeletedCount();
    }

    public long deleteByFriendRequestId(UUID id) {
        return mongoTemplate
                .remove(Notification.class)
                .matching(Criteria.where("friendRequest").is(id).and("type").is(Notification.Type.FRIEND_REQUEST))
                .one()
                .getDeletedCount();
    }


    public Notification save(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(Instant.now());
        }
        return mongoTemplate.save(notification);
    }
    
}
