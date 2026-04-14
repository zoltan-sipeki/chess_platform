package net.chess_platform.chat_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.User;

@Repository
public class UserRepository {

    private MongoOperations mongoTemplate;

    public UserRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(User user) {
        mongoTemplate.save(user);
    }

    public void replace(User user) {
        mongoTemplate.replace(new Query(Criteria.where("_id").is(user.getId())), user);
    }

    public boolean userExistsById(UUID userId) {
        return mongoTemplate.query(User.class).matching(Criteria.where("_id").is(userId)).exists();
    }

    public User findById(UUID userId) {
        return mongoTemplate.findById(userId, User.class);
    }

    public List<User> findByIds(List<UUID> userIds) {
        return mongoTemplate.query(User.class).matching(Criteria.where("_id").in((userIds))).all();
    }
}