package net.chess_platform.matchmaking_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.domain.UpdateSpecification.UpdateOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import net.chess_platform.matchmaking_service.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID>, JpaSpecificationExecutor<Player> {

    default long update(UUID id, Player.Update update) {
        if (update == null || id == null) {
            return 0;
        }

        UpdateOperation<Player> op = (root, u, cb) -> {
            var rankedMmr = update.getRankedMmr();
            if (rankedMmr != 0) {
                u.set(root.get("rankedMmr"), rankedMmr);
            }

            var unrankedMmr = update.getUnrankedMmr();
            if (unrankedMmr != 0) {
                u.set(root.get("unrankedMmr"), unrankedMmr);
            }

            var avatar = update.getAvatar();
            if (avatar != null) {
                u.set(root.get("avatar"), avatar);
            }

            var displayName = update.getDisplayName();
            if (displayName != null) {
                u.set(root.get("displayName"), displayName);
            }
        };

        return update(op.where((root, cb) -> cb.equal(root.get("id"), id)));
    }
}
