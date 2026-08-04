package net.chess_platform.chat_service.repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.chat_service.model.Message;

@Repository
public class MessageRepository {

	private static record UnreadCounts(UUID channelId, int unreadCount) {
	}

	private final MongoOperations mongoTemplate;

	public MessageRepository(MongoOperations mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public Map<UUID, Integer> countUnreadMessages(List<UUID> channelIds, UUID userId) {
		AggregationOperation match = context -> new Document()
				.append("$match",
						new Document().append("userId", userId).append("removed", new Document().append("$ne", true))
								.append("channel._id",
										new Document().append("$in", channelIds)));

		AggregationOperation lookup = context -> new Document()
				.append("$lookup",
						new Document()
								.append("from", "message").append("as", "messages").append(
										"let",
										new Document()
												.append("channelId", "$channel._id")
												.append("lastReadMessageSeq", "$lastReadMessageSeq"))
								.append("pipeline", List.of(
										new Document().append("$match",
												new Document().append("$expr", new Document().append("$and", List.of(
														new Document().append("$eq",
																List.of("$channelId", "$$channelId")),
														new Document().append("$gt",
																List.of("$sequenceNumber", "$$lastReadMessageSeq")))))),
										new Document().append("$count", "unreadCount"))));

		AggregationOperation project = context -> new Document()
				.append("$project", new Document().append("channelId", "$channel._id").append("unreadCount",
						new Document().append("$arrayElemAt", List.of("$messages.unreadCount", 0))));

		Aggregation a = Aggregation.newAggregation(
				match,
				lookup,
				project);

		var result = mongoTemplate.aggregate(
				a,
				ChannelMember.class,
				UnreadCounts.class).getMappedResults();

		return result.stream().collect(Collectors.toMap(k -> k.channelId(), v -> v.unreadCount()));
	}

	public List<Message> findAllWithSender(int limit, Criteria criteria) {
		var a = Aggregation.newAggregation(
				Aggregation.match(criteria),
				Aggregation.sort(Sort.by(Sort.Direction.ASC, "messageId")),
				Aggregation.lookup("user", "senderId", "_id", "sender"),
				Aggregation.sort(Direction.DESC, "sequenceNumber"),
				Aggregation.limit(limit),
				Aggregation.sort(Direction.ASC, "sequenceNumber"));

		return mongoTemplate.aggregate(a, Message.class, Message.class).getMappedResults();
	}

	public Message updateContent(Criteria criteria, String content) {
		var message = mongoTemplate.findAndModify(
				new Query(criteria),
				new Update().set("content", content).set("lastEditedAt", Instant.now()),
				FindAndModifyOptions.options().returnNew(true),
				Message.class);

		if (message == null) {
			return null;
		}

		return message;
	}

	public Message deleteOne(Criteria criteria) {
		var message = mongoTemplate
				.findAndRemove(new Query(criteria), Message.class);

		if (message == null) {
			return null;
		}

		return message;
	}

	public Message save(Message message) {
		return mongoTemplate.save(message);
	}
}
