package net.chess_platform.chat_service.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.ChannelMember;

@Repository
public class ChannelMemberRepository {

	private MongoOperations mongoTemplate;

	public ChannelMemberRepository(MongoOperations mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public long update(ChannelMember.Update update, Criteria criteria) {
		var u = new Update();

		var lastReadMessageId = update.getLastReadMessageSeq();
		if (lastReadMessageId != null) {
			u.set("lastReadMessageSeq", lastReadMessageId);
		}

		var lastReadableMessageId = update.getLastReadableMessageSeq();
		if (lastReadableMessageId != null) {
			u.set("lastReadableMessageSeq", lastReadableMessageId);
		}

		var removed = update.getRemoved();
		if (removed != null) {
			u.set("removed", removed);
		}

		var roles = update.getRoles();
		if (roles != null) {
			u.set("roles", roles);
		}

		return mongoTemplate
				.update(ChannelMember.class)
				.matching(criteria)
				.apply(u)
				.all()
				.getMatchedCount();	
	}

	public Collection<ChannelMember> saveAll(List<ChannelMember> members) {
		return mongoTemplate.insertAll(members);
	}

	public List<ChannelMember> findAll(List<UUID> channelIds, UUID userId) {
		return mongoTemplate.query(ChannelMember.class)
				.matching(Criteria.where("channel.id").in(channelIds).and("userId").is(userId)).all();
	}

	public Map<UUID, ChannelMember> findAll(UUID channelId) {
		var list = mongoTemplate.query(ChannelMember.class).matching(Criteria.where("channel.id").is(channelId)).all();
		return list.stream().collect(Collectors.toMap(ChannelMember::getUserId, m -> m));
	}

	public boolean hasChannelRoles(UUID userId, UUID channelId, List<String> roles) {
		return mongoTemplate.query(ChannelMember.class)
				.matching(Criteria.where("channel.id").is(channelId).and("userId").is(userId)
						.and("removed").is(false)
						.and("roles").in(roles))
				.exists();
	}

	public boolean isInChannel(UUID userId, UUID channelId) {
		return mongoTemplate.query(ChannelMember.class).matching(
				Criteria.where("channel.id").is(channelId).and("userId").is(userId)).exists();
	}

}
