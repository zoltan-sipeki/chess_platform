package net.chess_platform.chat_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.FriendRequest;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class FriendRequestRepository {

	private final MongoOperations mongoTemplate;

	public FriendRequestRepository(MongoOperations mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public boolean hasPending(UUID senderId, UUID receiverId) {
		return mongoTemplate.query(FriendRequest.class)
				.matching(Criteria.where("receiver").is(receiverId)
						.and("sender").is(senderId).and("status").is(FriendRequest.Status.PENDING))
				.exists();
	}

	public FriendRequest update(FriendRequest.Update update,
			Authorization auth) {
		MongoQueryFragment<FriendRequest> fragment = auth.getQueryFragment(FriendRequest.class);

		var u = new Update();
		var status = update.getStatus();
		if (status != null) {
			u.set("status", status);
		}

		return mongoTemplate
				.findAndModify(
						new Query(fragment.getCriteria()),
						u,
						FindAndModifyOptions.options().returnNew(true),
						FriendRequest.class);
	}

	public FriendRequest save(FriendRequest friendRequest, Authorization auth) {
		if (!auth.canCreate(friendRequest)) {
			return null;
		}

		return mongoTemplate.save(friendRequest);
	}

	public List<FriendRequest> findAll(Authorization auth) {
		MongoQueryFragment<FriendRequest> fragment = auth.getQueryFragment(FriendRequest.class);
		return mongoTemplate.query(FriendRequest.class).matching(fragment.getCriteria()).all();
	}

}
