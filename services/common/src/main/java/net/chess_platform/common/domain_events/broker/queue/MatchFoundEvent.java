package net.chess_platform.common.domain_events.broker.queue;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.BroadcastEvent;
import net.chess_platform.common.domain_events.broker.DomainEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent.Payload;

public class MatchFoundEvent extends BroadcastEvent<Payload> {

    public static class Payload {

        public static class Builder {

            private Payload instance;

            public Builder(String token, UUID target) {
                this.instance = new Payload(token, target);
            }

            public Builder inviter(User inviter) {
                instance.inviter = inviter;
                return this;
            }

            public Builder invitee(User invitee) {
                instance.invitee = invitee;
                return this;
            }

            public Payload build() {
                return instance;
            }
        }

        private User inviter;

        private User invitee;

        private UUID target;

        private String token;

        private String status = "PENDING";

        public Payload() {
        }

        private Payload(String token, UUID target) {
            this.token = token;
            this.target = target;
        }

        public User getInviter() {
            return inviter;
        }

        public User getInvitee() {
            return invitee;
        }

        public String getToken() {
            return token;
        }

        public UUID getTarget() {
            return target;
        }

        public String getStatus() {
            return status;
        }
    }

    public MatchFoundEvent(List<UUID> recipients, Payload data) {
        super(recipients, Category.QUEUE, DomainEvent.Type.MATCH_FOUND, data);
    }
}
