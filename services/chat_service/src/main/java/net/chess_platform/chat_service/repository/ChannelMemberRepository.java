package net.chess_platform.chat_service.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Channel;
import net.chess_platform.chat_service.model.ChannelMember;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class ChannelMemberRepository {

	private MongoOperations mongoTemplate;

	private ChannelRepository channelRepository;

	public ChannelMemberRepository(MongoOperations mongoTemplate, ChannelRepository channelRepository) {
		this.mongoTemplate = mongoTemplate;
		this.channelRepository = channelRepository;
	}

	public long updateLastReadMessage(UUID channelId, long lastReadMessageId, Authorization auth) {
		var channel = channelRepository.findChannelById(channelId);
		if (channel == null) {
			return 0;
		}

		long nextMessageId = channel.getNextMessageId();
		lastReadMessageId = lastReadMessageId >= nextMessageId ? nextMessageId : -1;

		MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);
		return mongoTemplate
				.update(ChannelMember.class)
				.matching(fragment.getCriteria())
				.apply(new Update().set("lastReadMessage", lastReadMessageId))
				.all()
				.getModifiedCount();
	}

	public long clearChannelHistory(UUID channelId, Authorization auth) {
		var channel = channelRepository.findChannelById(channelId);
		if (channel == null) {
			return 0;
		}

		long firstMessageId = channel.getFirstMessageId();
		long nextMessageId = channel.getNextMessageId();

		MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);
		return mongoTemplate
				.update(ChannelMember.class)
				.matching(fragment.getCriteria())
				.apply(new Update()
						.set("lastReadMessageId",
								nextMessageId - firstMessageId > 0 ? nextMessageId - 1
										: firstMessageId)
						.set("lastReadableMessageId", nextMessageId))
				.all()
				.getModifiedCount();
	}

	public long kickMemberFromGroupChannel(Authorization auth) {
		MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);

		return mongoTemplate
				.update(ChannelMember.class)
				.matching(Criteria.where("channel.type").is(Channel.Type.GROUP).andOperator(fragment.getCriteria()))
				.apply(new Update()
						.set("removed", true)
						.set("roles", new ArrayList<>()))
				.all()
				.getModifiedCount();
	}

	public List<ChannelMember> saveAll(List<ChannelMember> members, Authorization auth) {
		for (var member : members) {
			if (!auth.canCreate(member)) {
				return null;
			}
		}

		var list = mongoTemplate.insert(members, ChannelMember.class);
		var ids = list.stream().map(ChannelMember::getUserId).toList();
		return findWithUser(ids);
	}

	private List<ChannelMember> findWithUser(List<UUID> memberIds) {
		var a = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("userId").in(memberIds)),
				Aggregation.lookup("user", "userId", "_id", "user"));

		return mongoTemplate.aggregate(a, ChannelMember.class, ChannelMember.class).getMappedResults();
	}

	public Map<UUID, ChannelMember> findByChannelId(UUID channelId) {
		var list = mongoTemplate.query(ChannelMember.class).matching(Criteria.where("channel.id").is(channelId)).all();
		return list.stream().collect(Collectors.toMap(ChannelMember::getUserId, m -> m));
	}

	public long updateMessageIdsByChannelIdAndUserId(UUID channelId, UUID senderId, long lastMessageId) {
		return mongoTemplate
				.update(ChannelMember.class)
				.matching(Criteria.where("channel.id").is(channelId).and("userId").is(senderId))
				.apply(new Update().set("lastReadMessageId", lastMessageId))
				.all()
				.getModifiedCount();
	}

	public boolean hasChannelRoles(UUID userId, UUID channelId, List<String> roles) {
		return mongoTemplate.query(ChannelMember.class)
				.matching(Criteria.where("channel.id").is(channelId).and("userId").is(userId)
						.and("removed").is(false)
						.and("roles").in(roles))
				.exists();
	}

	public UUID findDMByRecipientIds(UUID userId1, UUID userId2) {
		var a = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("channel.type").is(Channel.Type.DM).and("userId")
						.in(userId1, userId2)),
				Aggregation.group("channel.id").addToSet("userId").as("members"),
				Aggregation.match(Criteria.where("members").all(userId1, userId2)),
				Aggregation.project().and("_id").as("channel.id"));

		var result = mongoTemplate.aggregate(a, ChannelMember.class, HasDMResult.class);
		return result.getUniqueMappedResult().channelId();
	}

	public boolean isInChannel(UUID userId, UUID channelId) {
		return mongoTemplate.query(ChannelMember.class).matching(
				Criteria.where("channel.id").is(channelId).and("userId").is(userId)).exists();
	}

	private static record HasDMResult(UUID channelId) {
	}

	public long leaveGroupChannel(Authorization auth) {
		MongoQueryFragment<ChannelMember> fragment = auth.getQueryFragment(ChannelMember.class);

		return mongoTemplate.update(ChannelMember.class)
				.matching(Criteria.where("channel.type").is(Channel.Type.GROUP).andOperator(fragment.getCriteria()))
				.apply(new Update().set("removed", true))
				.all()
				.getModifiedCount();
	}
}
