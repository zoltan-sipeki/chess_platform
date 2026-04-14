package net.chess_platform.chat_service.repository;

import static net.chess_platform.chat_service.model.Notification.FriendRequestDetails.Status.PENDING;
import static net.chess_platform.chat_service.model.Notification.Type.FRIEND_REQUEST;

import java.util.UUID;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import net.chess_platform.chat_service.model.Notification;
import net.chess_platform.chat_service.model.Notification.FriendRequestDetails;
import net.chess_platform.common.permission.Authorization;
import net.chess_platform.common.permission.MongoQueryFragment;

@Repository
public class FriendRequestRepository {

	private MongoOperations mongoTemplate;

	private UserRepository userRepository;

	public FriendRequestRepository(MongoOperations mongoTemplate, UserRepository userRepository) {
		this.mongoTemplate = mongoTemplate;
		this.userRepository = userRepository;
	}

	public boolean hasPending(UUID senderId, UUID receiverId) {
		return mongoTemplate.query(Notification.class)
				.matching(Criteria.where("type").is(FRIEND_REQUEST).and("receiverId").is(receiverId)
						.and("senderId").is(senderId).and("details.status").is(PENDING))
				.exists();
	}

	public Notification updateStatusForFriendRequest(FriendRequestDetails.Status status,
			Authorization auth) {
		MongoQueryFragment<Notification> fragment = auth.getQueryFragment(Notification.class);

		return mongoTemplate
				.findAndModify(
						new Query(Criteria.where("type").is(FRIEND_REQUEST).andOperator(fragment.getCriteria())),
						new Update().set("details.status", status),
						FindAndModifyOptions.options().returnNew(true),
						Notification.class);
	}

	public Notification save(Notification friendRequest, Authorization auth) {
		if (!auth.canCreate(friendRequest)) {
			return null;
		}

		var r = mongoTemplate.save(friendRequest);
		var sender = userRepository.findById(friendRequest.getSenderId());
		r.setSender(sender);
		return r;
	}

}
