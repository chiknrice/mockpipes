package org.chiknrice.pipes;

import org.chiknrice.pipes.api.MessageMatcher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class ExpectedMessage<M> {

    private final MessageMatcher<M> messageMatcher;
    private final long timeout;
    private final CountDownLatch matched;

    ExpectedMessage(MessageMatcher<M> messageMatcher, long timeout) {
        this.messageMatcher = messageMatcher;
        this.timeout = timeout;
        this.matched = new CountDownLatch(1);
    }

    boolean matches(M message) {
        if (messageMatcher.matches(message)) {
            matched.countDown();
            return true;
        }
        return false;
    }

    void await() {
        try {
            if (!matched.await(timeout, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for matching " + messageMatcher);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
