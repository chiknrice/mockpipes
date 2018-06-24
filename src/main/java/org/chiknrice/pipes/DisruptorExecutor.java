package org.chiknrice.pipes;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class DisruptorExecutor {

    private final BlockingWaitStrategy waitStrategy;
    private final Disruptor<Event> disruptor;

    DisruptorExecutor(String threadName) {
        waitStrategy = new BlockingWaitStrategy();
        this.disruptor = new Disruptor<>(Event::new, 1024, r -> {
            return new Thread(r, threadName);
        }, ProducerType.SINGLE, waitStrategy);
        this.disruptor.handleEventsWith((event, sequence, endOfBatch) -> event.runnable.run());
        disruptor.start();
    }

    void submit(Runnable runnable) {
        disruptor.publishEvent((event, sequence, r) -> event.runnable = r, runnable);
    }

    void wakeUp() {
        waitStrategy.signalAllWhenBlocking();
    }

    static class Event {
        Runnable runnable;
    }

    public static void main(String[] args) throws TimeoutException {
        DisruptorExecutor eventProcessor = new DisruptorExecutor("test");
        IntStream.range(0, 1000).forEach(i -> eventProcessor.submit(() -> System.out.printf("[%s] - %s\n", Thread.currentThread().getName(), i)));
        eventProcessor.disruptor.shutdown(10, TimeUnit.SECONDS);
    }

}
