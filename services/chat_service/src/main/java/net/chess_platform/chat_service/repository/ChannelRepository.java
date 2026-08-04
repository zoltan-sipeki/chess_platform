package net.chess_platform.chat_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Channel;

@Repository
public class ChannelRepository {

    private MongoOperations mongoTemplate;

    public ChannelRepository(MongoOperations mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Channel> findAllWithMembers(Criteria criteria) {
        var a = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.lookup("user", "memberIds", "_id", "members"));

        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getMappedResults();
    }

    public long updateName(Criteria criteria, String name) {
        return mongoTemplate.update(Channel.class).matching(criteria).apply(new Update().set("name", name)).all()
                .getModifiedCount();
    }

    public Channel save(Channel channel) {
        return mongoTemplate.save(channel);
    }

    public long getNextMessageSeq(UUID channelId) {
        return mongoTemplate
                .findAndModify(new Query(Criteria.where("_id").is(channelId)), new Update().inc("nextMessageSeq", 1),
                        FindAndModifyOptions.options().returnNew(false), Channel.class)
                .getNextMessageSeq();
    }

    public List<Channel> findAll(UUID memberId) {
        return mongoTemplate.find(new Query(Criteria.where("memberIds").is(memberId)), Channel.class);
    }

    public Channel findOneWithMembers(Channel.Type type, List<UUID> members) {
        var a = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("type").is(type).and("memberIds").all(members)),
                Aggregation.lookup("user", "memberIds", "_id", "members"));

        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getUniqueMappedResult();
    }

    public Channel findOne(UUID id) {
        return mongoTemplate.findById(id, Channel.class);
    }

    public Channel findOneWithMembers(UUID id) {
        var a = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.lookup("user", "memberIds", "_id", "members"));

        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getUniqueMappedResult();
    }

    public Channel findOne(Channel.Type type, UUID id) {
        return mongoTemplate.findOne(new Query(Criteria.where("type").is(type).and("_id").is(id)), Channel.class);
    }

    public Channel findOne(Criteria criteria) {
        return mongoTemplate.findOne(new Query(criteria), Channel.class);
    }

    public long removeMember(UUID channelId, UUID userId) {
        return mongoTemplate
                .update(Channel.class)
                .matching(Criteria.where("type").is(Channel.Type.GROUP).and("_id").is(channelId).and("memberIds")
                        .is(userId))
                .apply(new Update().pull("memberIds", userId))
                .all()
                .getModifiedCount();
    }

    public long addMembers(UUID channelId, List<UUID> userIds) {
        return mongoTemplate
                .update(Channel.class)
                .matching(Criteria.where("type").is(Channel.Type.GROUP).and("_id").is(channelId))
                .apply(new Update().addToSet("memberIds", userIds))
                .all()
                .getModifiedCount();
    }
}
