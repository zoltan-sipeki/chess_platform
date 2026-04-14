package net.chess_platform.chat_service.repository;

import java.util.UUID;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Privacy;

@Repository
public class PrivacyRepository {

    private MongoOperations mongoTemplate;

    public PrivacyRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Privacy findByUserId(UUID userId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("userId").is(userId)), Privacy.class);
    }

    public Privacy save(Privacy privacy) {
        return mongoTemplate.save(privacy);
    }
}
