package net.chess_platform.chat_service.repository;

import java.util.UUID;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.NotificationMetadata;

@Repository
public class NotificationMetadataRepository {

    private final MongoOperations mongoTemplate;

    public NotificationMetadataRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public NotificationMetadata findOne(UUID receiverId) {
        return mongoTemplate.query(NotificationMetadata.class).matching(Criteria.where("receiver").is(receiverId))
                .oneValue();
    }

    public long updateLastReadSequenceNumber(Criteria criteria, long lastReadSequenceNumber) {
        return mongoTemplate.updateFirst(new Query(criteria),
                new Update().set("lastReadSequenceNumber", lastReadSequenceNumber), NotificationMetadata.class)
                .getModifiedCount();
    }

    public NotificationMetadata save(NotificationMetadata notificationMetadata) {
        return mongoTemplate.save(notificationMetadata);
    }

    public long getNextSequenceNumber(UUID userId) {
        var metadata = mongoTemplate.findAndModify(new Query(Criteria.where("receiver").is(userId)),
                new Update().inc("sequenceNumber", 1),
                FindAndModifyOptions.options().returnNew(true), NotificationMetadata.class);
        return metadata.getSequenceNumber();
    }
}
