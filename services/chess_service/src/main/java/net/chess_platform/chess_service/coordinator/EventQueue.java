package net.chess_platform.chess_service.coordinator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class EventQueue {

    private BlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void offer(Object message) {
        queue.offer(message);
    }

    public Object take() throws InterruptedException {
        return queue.take();
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return scheduler.schedule(runnable, delay, unit);
    }

    public ScheduledFuture<?> schedule(Supplier<Object> supplier, long delay, TimeUnit unit) {
        return schedule(() -> offer(supplier.get()), delay, unit);
    }
}
