package net.chess_platform.chat_service.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Message;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class MessageRepository {

    private MongoOperations mongoTemplate;

    private UserRepository userRepository;

    private ChannelMemberRepository channelMemberRepository;

    private ChannelRepository channelRepository;

    public MessageRepository(MongoOperations mongoTemplate, UserRepository userRepository,
            ChannelRepository channelRepository, ChannelMemberRepository channelMemberRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.channelMemberRepository = channelMemberRepository;
        this.channelRepository = channelRepository;
    }

    public List<Message> findAll(int limit, Authorization auth) {
        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);

        var a = Aggregation.newAggregation(
                Aggregation.match(fragment.getCriteria()),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "messageId")),
                Aggregation.lookup("user", "senderId", "_id", "sender"),
                Aggregation.limit(limit));

        return mongoTemplate.aggregate(a, Message.class, Message.class).getMappedResults();
    }

    public Message updateContent(String content,
            Authorization auth) {
        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);

        var message = mongoTemplate.findAndModify(
                new Query(fragment.getCriteria()),
                new Update().set("content", content).set("lastEditedAt", OffsetDateTime.now()),
                FindAndModifyOptions.options().returnNew(true),
                Message.class);

        if (message == null) {
            return null;
        }

        var sender = userRepository.findById(message.getSenderId());
        message.setSender(sender);
        return message;
    }

    public Message save(Message message, Authorization auth) {
        if (!auth.canCreate(message)) {
            return null;
        }

        long nextMessageId = channelRepository.getNextMessageId(message.getChannelId());
        message.setMessageId(nextMessageId);

        var m = mongoTemplate.save(message);
        channelMemberRepository.updateMessageIdsByChannelIdAndUserId(message.getChannelId(), message.getSenderId(),
                message.getMessageId());

        var sender = userRepository.findById(message.getSenderId());
        m.setSender(sender);

        return m;
    }

    public Message delete(Authorization auth) {
        MongoQueryFragment<Message> fragment = auth.getQueryFragment(Message.class);

        var message = mongoTemplate
                .findAndRemove(new Query(fragment.getCriteria()), Message.class);

        if (message == null) {
            return null;
        }

        var sender = userRepository.findById(message.getSenderId());
        message.setSender(sender);
        return message;
    }

    public Message findLastMessageInChannel(UUID channelId) {
        var a = Aggregation.newAggregation(
                Aggregation.match(where("channelId").is(channelId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "messageId")),
                Aggregation.limit(1));

        return mongoTemplate.aggregate(a, Message.class, Message.class).getUniqueMappedResult();
    }
}
