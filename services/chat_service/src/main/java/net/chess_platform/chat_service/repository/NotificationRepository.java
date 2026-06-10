package net.chess_platform.chat_service.repository;

import java.time.OffsetDateTime;
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
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class NotificationRepository {

    private final MongoOperations mongoTemplate;

    public NotificationRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Notification> findAll(Authorization auth, Long before, long limit) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

        var query = fragment.getCriteria();
        if (before != null) {
            query = query.and("sequenceNumber").lt(before);
        }

        var a = Aggregation.newAggregation(
                Aggregation.match(query),
                Aggregation.sort(Sort.by(Direction.DESC, "sequenceNumber")),
                Aggregation.limit(limit));

        return mongoTemplate.aggregate(a, Notification.class, Notification.class).getMappedResults();

    }

    public long countUnread(Authorization auth) {
        MongoQueryFragment<Notification> f1 = auth.getQueryFragment(Notification.class);
        MongoQueryFragment<NotificationMetadata> f2 = auth.getQueryFragment(NotificationMetadata.class);

        var metadata = mongoTemplate
                .findOne(new Query(f2.getCriteria()), NotificationMetadata.class);

        return mongoTemplate.count(
                new Query(Criteria.where("sequenceNumber").gt(metadata.getLastReadSequenceNumber())
                        .andOperator(f1.getCriteria())),
                Notification.class);
    }

    public long getLastReadSequenceNumber(Authorization auth) {
        MongoQueryFragment<NotificationMetadata> fragment = auth.getQueryFragment(NotificationMetadata.class);
        return mongoTemplate
                .findOne(new Query(fragment.getCriteria()), NotificationMetadata.class)
                .getLastReadSequenceNumber();
    }

    public long deleteOne(Authorization auth) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);
        return mongoTemplate
                .remove(Notification.class)
                .matching(fragment.getCriteria())
                .one()
                .getDeletedCount();
    }

    public long deleteAll(Authorization auth) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);
        return mongoTemplate
                .remove(Notification.class)
                .matching(fragment.getCriteria())
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

    public long getNextSequenceNumber(UUID userId) {
        var metadata = mongoTemplate.findAndModify(new Query(Criteria.where("receiver").is(userId)),
                new Update().inc("sequenceNumber", 1),
                FindAndModifyOptions.options().returnNew(true), NotificationMetadata.class);
        return metadata.getSequenceNumber();
    }

    public Notification save(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(OffsetDateTime.now());
        }
        return mongoTemplate.save(notification);
    }

    public NotificationMetadata save(NotificationMetadata notificationMetadata) {
        return mongoTemplate.save(notificationMetadata);
    }

    public long updateAll(Notification.Update update, Authorization auth) {
        MongoQueryFragment<NotificationMetadata> fragment = auth.getQueryFragment(NotificationMetadata.class);

        var u = new Update();

        Long seq = update.getLastReadSequenceNumber();
        if (seq != null) {
            u.set("lastReadSequenceNumber", seq);
            return mongoTemplate.updateFirst(new Query(fragment.getCriteria()), u, NotificationMetadata.class)
                    .getModifiedCount();
        }

        return 0;
    }
}
