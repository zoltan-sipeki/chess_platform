package net.chess_platform.chat_service.repository;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    public Page<Notification> findAll(Authorization auth, Pageable pageable) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

        var a = Aggregation.newAggregation(
                Aggregation.match(fragment.getCriteria()),
                Aggregation.skip(pageable.getOffset()),
                Aggregation.limit(pageable.getPageSize()));

        var result = mongoTemplate.aggregate(a, Notification.class, Notification.class).getMappedResults();
        var count = mongoTemplate.count(new Query(fragment.getCriteria()), Notification.class);

        return new PageImpl<>(result, pageable, count);

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
            u.set("read", seq);
            return mongoTemplate.updateFirst(new Query(fragment.getCriteria()), u, NotificationMetadata.class)
                    .getModifiedCount();
        }

        return 0;
    }
}
