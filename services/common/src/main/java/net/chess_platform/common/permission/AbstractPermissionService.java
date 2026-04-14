package net.chess_platform.common.permission;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import net.chess_platform.common.security.CurrentUser;

public abstract class AbstractPermissionService<A extends Enum<A>> {

    public interface Policy {
        Authorization authorize(CurrentUser user, Map<String, Object> attributes);
    }

    private Map<A, Policy> policies = new HashMap<>();

    @PostConstruct
    public void init() {
        registerPolicies();
    }

    public Authorization authorize(A action, CurrentUser user, Map<String, Object> attributes) {
        var policy = policies.get(action);
        if (policy == null) {
            return new Authorization();
        }

        return policy.authorize(user, attributes);
    }

    public void registerPolicy(A action, Policy policy) {
        policies.put(action, policy);
    }

    protected abstract void registerPolicies();
}
