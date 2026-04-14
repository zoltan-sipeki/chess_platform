package net.chess_platform.gateway.util;

import java.util.function.Supplier;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextAwareAsyncSupplier<T> implements Supplier<T> {

    private SecurityContext securityContext;

    private Supplier<T> supplier;

    public SecurityContextAwareAsyncSupplier(Supplier<T> supplier) {
        securityContext = SecurityContextHolder.getContext();
        this.supplier = supplier;
    }

    @Override
    public T get() {
        SecurityContextHolder.setContext(securityContext);
        try {
            return supplier.get();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
