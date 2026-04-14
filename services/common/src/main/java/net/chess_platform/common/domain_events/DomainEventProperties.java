package net.chess_platform.common.domain_events;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "domain-events")
public class DomainEventProperties {

    private long retryIntervalMs;

    public DomainEventProperties(@DefaultValue("60000") long intervalMs) {
        this.retryIntervalMs = intervalMs;
    }

    public long getRetryIntervalMs() {
        return retryIntervalMs;
    }
}
