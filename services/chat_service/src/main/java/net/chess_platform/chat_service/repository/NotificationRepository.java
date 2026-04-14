package net.chess_platform.chat_service.repository;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class NotificationRepository {

    private final MongoOperations mongoTemplate;

    public NotificationRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Notification> findAll(Authorization auth) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);
        return mongoTemplate.query(Notification.class).matching(fragment.getCriteria()).all();
    }

    public long delete(Authorization auth) {
        MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);
        return mongoTemplate
                .remove(Notification.class)
                .matching(fragment.getCriteria())
                .all()
                .getDeletedCount();
    }

    public Notification save(Notification notification) {
        return mongoTemplate.save(notification);
    }

}
