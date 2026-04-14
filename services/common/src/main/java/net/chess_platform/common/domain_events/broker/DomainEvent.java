package net.chess_platform.common.domain_events.broker;

import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.GROUP_CHANNEL_CREATED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.GROUP_CHANNEL_MEMBER_JOINED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.GROUP_CHANNEL_MEMBER_LEFT;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.GROUP_CHANNEL_ROLE_CHANGED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MATCH_ENDED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MATCH_FOUND;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MATCH_FOUND_BROADCAST;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MATCH_STARTED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MESSAGE_CREATED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.MESSAGE_EDITED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.NOTIFICATION;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.PRESENCE_CHANGED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.UNFRIEND;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.USER_CREATED;
import static net.chess_platform.common.domain_events.broker.DomainEvent.Type.USER_UPDATED;

import java.util.Map;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.chat.GroupChannelCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberJoinedEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelMemberLeftEvent;
import net.chess_platform.common.domain_events.broker.chat.GroupChannelRoleChangedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageCreatedEvent;
import net.chess_platform.common.domain_events.broker.chat.MessageEditedEvent;
import net.chess_platform.common.domain_events.broker.chat.NotificationEvent;
import net.chess_platform.common.domain_events.broker.chat.UnfriendEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchEndedEvent;
import net.chess_platform.common.domain_events.broker.chess.MatchStartedEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundBroadcastEvent;
import net.chess_platform.common.domain_events.broker.queue.MatchFoundEvent;
import net.chess_platform.common.domain_events.broker.relay.PresenceChangedEvent;
import net.chess_platform.common.domain_events.broker.user.UserCreatedEvent;
import net.chess_platform.common.domain_events.broker.user.UserUpdatedEvent;

public class DomainEvent<T> {

    public static Map<Type, Class<? extends DomainEvent>> EVENT_TYPES = Map.ofEntries(
            Map.entry(MATCH_STARTED, MatchStartedEvent.class),
            Map.entry(MATCH_ENDED, MatchEndedEvent.class),
            Map.entry(USER_CREATED, UserCreatedEvent.class),
            Map.entry(USER_UPDATED, UserUpdatedEvent.class),
            Map.entry(MATCH_FOUND, MatchFoundEvent.class),
            Map.entry(MATCH_FOUND_BROADCAST, MatchFoundBroadcastEvent.class),
            Map.entry(MESSAGE_CREATED, MessageCreatedEvent.class),
            Map.entry(MESSAGE_EDITED, MessageEditedEvent.class),
            Map.entry(GROUP_CHANNEL_CREATED, GroupChannelCreatedEvent.class),
            Map.entry(GROUP_CHANNEL_MEMBER_JOINED, GroupChannelMemberJoinedEvent.class),
            Map.entry(GROUP_CHANNEL_MEMBER_LEFT, GroupChannelMemberLeftEvent.class),
            Map.entry(GROUP_CHANNEL_ROLE_CHANGED, GroupChannelRoleChangedEvent.class),
            Map.entry(NOTIFICATION, NotificationEvent.class),
            Map.entry(UNFRIEND, UnfriendEvent.class),
            Map.entry(PRESENCE_CHANGED, PresenceChangedEvent.class));

    public enum Category {
        CHESS,
        USER,
        QUEUE,
        SOCIAL,
        COMMON,
        RELAY,
    }

    public enum Type {
        MATCH_STARTED,
        MATCH_ENDED,
        MATCH_FOUND,
        MATCH_FOUND_BROADCAST,
        MATCH_CLOSED,

        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_STATUS_UPDATED,

        DEQUEUE,
        ENQUEUE,

        MESSAGE_CREATED,
        MESSAGE_EDITED,
        MESSAGE_DELETED,

        GROUP_CHANNEL_CREATED,
        GROUP_CHANNEL_MEMBER_JOINED,
        GROUP_CHANNEL_MEMBER_LEFT,
        GROUP_CHANNEL_ROLE_CHANGED,

        NOTIFICATION,

        UNFRIEND,

        PRESENCE_CHANGED
    }

    private UUID id = UUID.randomUUID();

    protected Category category;

    protected Type type;

    private T data;

    protected DomainEvent(Category category) {
        this.category = category;
    }

    protected DomainEvent(Category category, Type type) {
        this.category = category;
        this.type = type;
    }

    protected DomainEvent(Category category, Type type, T data) {
        this.category = category;
        this.type = type;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public Type getType() {
        return type;
    }

    public T getData() {
        return data;
    }

}
