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
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class ChannelRepository {

    private MongoOperations mongoTemplate;

    private UserRepository userRepository;

    public ChannelRepository(MongoOperations mongoTemplate, UserRepository userRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
    }

    public List<Channel> findAll(Authorization auth) {
        MongoQueryFragment<Channel> fragment =auth.getQueryFragment(Channel.class);

        var a = Aggregation.newAggregation(
                Aggregation.match(fragment.getCriteria()),
                Aggregation.lookup("user", "memberIds", "_id", "members"));
        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getMappedResults();
    }

    public Channel updateName(String name, Authorization auth) {
        MongoQueryFragment<Channel> fragment =auth.getQueryFragment(Channel.class);

        var channel = mongoTemplate.findAndModify(
                new Query(fragment.getCriteria()),
                new Update().set("name", name), FindAndModifyOptions.options().returnNew(true),
                Channel.class);
        return channel;
    }

    public Channel save(Channel channel, Authorization auth) {
        if (!auth.canCreate(channel)) {
            return null;
        }

        var result = mongoTemplate.save(channel);
        var members = userRepository.findByIds(result.getMemberIds());
        result.setMembers(members);
        return result;
    }

    public List<Channel> findGroupChannelByMemberId(UUID memberId) {
        return mongoTemplate.query(Channel.class)
                .matching(Criteria.where("memberIds").is(memberId).and("type").is(Channel.Type.GROUP)).all();
    }

    public Channel findGroupChannelById(UUID channelId) {
        return mongoTemplate.query(Channel.class)
                .matching(Criteria.where("channelId").is(channelId).and("type").is(Channel.Type.GROUP)).oneValue();
    }

    public Channel findChannelById(UUID channelId) {
        return mongoTemplate.findById(channelId, Channel.class);
    }

    public long getNextMessageId(UUID channelId) {
        return mongoTemplate
                .findAndModify(new Query(Criteria.where("_id").is(channelId)), new Update().inc("nextMessageId", 1),
                        FindAndModifyOptions.options().returnNew(false), Channel.class)
                .getNextMessageId();
    }

    public Channel findDMChannelWithMembers(UUID memberId1, UUID memberId2) {
        var a = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("memberIds").all(memberId1, memberId2)),
                Aggregation.lookup("user", "memberIds", "_id", "members"));

        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getUniqueMappedResult();
    }

    public long kickMemberFromGroupChannel(UUID channelId, UUID userId) {
        return mongoTemplate
                .update(Channel.class)
                .matching(Criteria.where("type").is(Channel.Type.GROUP).and("_id").is(channelId).and("memberIds")
                        .is(userId))
                .apply(new Update().pull("memberIds", userId))
                .all()
                .getModifiedCount();
    }

    public Channel findGroupChannelWithMembersById(UUID channelId) {
        var a = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(channelId)),
                Aggregation.lookup("user", "memberIds", "_id", "members"));

        return mongoTemplate.aggregate(a, Channel.class, Channel.class).getUniqueMappedResult();
    }
}
