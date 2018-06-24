package org.chiknrice.pipes;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class ProducerSynchronizedExecutor<T> {

    private final Map<T, CompletableFuture<?>> producerLastFuture = new ConcurrentHashMap<>();
    private final AtomicInteger threadIndex = new AtomicInteger(0);
    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> new Thread(r, "executor-" + threadIndex.getAndIncrement()));

    // ensures that any task submitted for a specific producer is serially executed
    void submit(T producer, Runnable task) {
        synchronized (producer) {
            CompletableFuture<?> lastFuture = producerLastFuture.computeIfAbsent(producer, CompletableFuture::completedFuture);
            CompletableFuture<?> nextFuture = lastFuture.thenApplyAsync(result -> {
                task.run();
                return null;
            }, executorService);
            producerLastFuture.put(producer, nextFuture);
        }
    }

}
