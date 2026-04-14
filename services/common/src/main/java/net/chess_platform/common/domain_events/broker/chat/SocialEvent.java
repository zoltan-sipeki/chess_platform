package net.chess_platform.common.domain_events.broker.chat;

import java.util.List;
import java.util.UUID;

import net.chess_platform.common.domain_events.broker.DomainEvent;

public class SocialEvent<T> extends DomainEvent<T> {

	private List<UUID> recipients;

	public SocialEvent(List<UUID> recipients, Type type) {
		super(DomainEvent.Category.SOCIAL, type);
	}

	public SocialEvent(List<UUID> recipients, Type type, T data) {
		super(DomainEvent.Category.SOCIAL, type, data);
		this.recipients = recipients;
	}

	public List<UUID> getRecipients() {
		return recipients;
	}
}
