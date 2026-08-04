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

@Repository
public class FriendRequestRepository {

	private final MongoOperations mongoTemplate;

	public FriendRequestRepository(MongoOperations mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	public FriendRequest findPending(UUID senderId, UUID receiverId) {
		return mongoTemplate.query(FriendRequest.class)
				.matching(Criteria.where("status").is(FriendRequest.Status.PENDING)
						.orOperator(Criteria.where("receiver").is(receiverId)
								.and("sender").is(senderId),
								Criteria.where("receiver").is(senderId).and("sender").is(receiverId)))
				.oneValue();
	}

	public FriendRequest updateStatus(
			Criteria criteria, FriendRequest.Status status) {
		return mongoTemplate
				.findAndModify(
						new Query(criteria),
						new Update().set("status", status),
						FindAndModifyOptions.options().returnNew(true),
						FriendRequest.class);
	}

	public FriendRequest save(FriendRequest friendRequest) {
		return mongoTemplate.save(friendRequest);
	}

	public List<FriendRequest> findAll(Criteria criteria) {
		return mongoTemplate.query(FriendRequest.class).matching(criteria).all();
	}

}
