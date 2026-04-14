package net.chess_platform.common.permission;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Authorization {

    public static class MissingAuthorizationException extends RuntimeException {

        public MissingAuthorizationException(String message) {
            super(message);
        }
    }

    private String action;

    private BooleanSupplier allowed;

    private Boolean _allowed;

    private Map<Class<?>, QueryFragment<?>> queryFragments = new HashMap<>();

    private Map<Class<?>, Predicate<?>> createChecks = new HashMap<>();

    public Authorization() {
    }

    public void setAction(Object action) {
        this.action = action.toString();
    }

    @SuppressWarnings("unchecked")
    public <T> boolean canCreate(T entity) {
        var clazz = entity.getClass();
        var check = createChecks.get(clazz);
        if (check == null) {
            throw new MissingAuthorizationException(
                    "Missing predicate authorization for action " + action + " and for entity "
                            + clazz.getCanonicalName());
        }

        return isAllowed() && ((Predicate<T>) check).test(entity);
    }

    public boolean isAllowed() {
        if (_allowed != null) {
            return _allowed;
        }

        if (allowed == null) {
            throw new MissingAuthorizationException("Missing boolean authorization for action " + action);
        }

        _allowed = allowed.getAsBoolean();
        return _allowed;
    }

    @SuppressWarnings("unchecked")
    public <T, R extends QueryFragment<T>> R getQueryFragment(Class<T> entityClass) {
        var fragment = queryFragments.get(entityClass);
        if (fragment == null) {
            throw new MissingAuthorizationException(
                    "Missing query condition authorization for action " + action + " and for entity "
                            + entityClass.getCanonicalName());
        }

        return (R) fragment;
    }

    public <T> void setQueryCondition(Class<T> entityClass, QueryFragment<T> fragment) {
        queryFragments.put(entityClass, fragment);
    }

    public void setAllowed(BooleanSupplier supplier) {
        allowed = supplier;
    }

    public <T> void setCreateCheck(Class<T> entityClass, Predicate<T> predicate) {
        createChecks.put(entityClass, predicate);
    }

    public String getAction() {
        return action;
    }

}