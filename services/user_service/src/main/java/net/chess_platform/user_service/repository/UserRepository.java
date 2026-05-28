package net.chess_platform.user_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.UpdateSpecification.UpdateOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.chess_platform.user_service.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.displayName LIKE :prefix%")
    Page<User> findByDisplayNamePrefix(String prefix, Pageable pageable);

    @Transactional
    default User updateAndFetch(User.Update user) {
        if (user == null || user.getId() == null) {
            return null;
        }

        UpdateOperation<User> op = (root, update, cb) -> {
            var username = user.getUsername();
            if (username != null) {
                update.set(root.get("username"), username);
            }

            var displayName = user.getDisplayName();
            if (displayName != null) {
                update.set(root.get("displayName"), displayName);
            }

            var avatar = user.getAvatar();
            if (avatar != null) {
                update.set(root.get("avatar"), avatar);
            }

            var email = user.getEmail();
            if (email != null) {
                update.set(root.get("email"), email);
            }
        };

        update(op.where((root, cb) -> cb.equal(root.get("id"), user.getId())));

        return findById(user.getId()).orElse(null);
    }
}
