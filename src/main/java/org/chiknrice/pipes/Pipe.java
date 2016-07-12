package org.chiknrice.pipes;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Pipe {

    private static final Logger LOG = LoggerFactory.getLogger(Pipe.class);

    private final IoSession session;

    private final Map<MessageMatcher, CountDownLatch> expectedMessages;
    private final Set<Object> received;
    private final Set<Object> sent;
    private final Set<Throwable> sendingExceptions;

    public Pipe(IoSession session) {
        this.session = session;
        this.expectedMessages = new ConcurrentHashMap<>();
        this.received = new ConcurrentHashSet<>();
        this.sent = new ConcurrentHashSet<>();
        this.sendingExceptions = new ConcurrentHashSet<>();
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public void send(Object message) {
        LOG.debug("Sending message \"{}\"", message);
        session.write(message).addListener(result -> {
            WriteFuture write = (WriteFuture) result;
            if (write.isWritten()) {
                sent.add(message);
            } else {
                Throwable e = write.getException();
                if (e != null) {
                    sendingExceptions.add(e);
                }
            }
        });
    }

    public void expect(MessageMatcher messageMatcher, long timeout) {
        for (Object message : received) {
            if (messageMatcher.matches(message)) {
                // Message already received
                return;
            }
        }
        CountDownLatch messageReceived = new CountDownLatch(1);
        expectedMessages.put(messageMatcher, messageReceived);
        boolean received;
        try {
            received = messageReceived.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        if (!received) {
            expectedMessages.remove(messageMatcher);
            throw new RuntimeException("Timeout waiting for message: " + messageMatcher.toString());
        }
    }

    public void onMessageReceived(Object message) {
        received.add(message);
        expectedMessages.entrySet().removeIf(entry -> {
            if (entry.getKey().matches(message)) {
                entry.getValue().countDown();
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pipe) {
            return session.equals(((Pipe) obj).session);
        } else {
            return false;
        }
    }

    public Map<MessageMatcher, CountDownLatch> getExpectedMessages() {
        return expectedMessages;
    }

    public Set<Object> getReceived() {
        return received;
    }

    public Set<Object> getSent() {
        return sent;
    }

}
