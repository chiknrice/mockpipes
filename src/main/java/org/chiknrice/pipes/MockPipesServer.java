package org.chiknrice.pipes;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class MockPipesServer extends IoHandlerAdapter implements MockPipes {

    private static final Logger LOG = LoggerFactory.getLogger(MockPipesServer.class);

    private final String host;
    private final int port;
    private final NioSocketAcceptor socketAcceptor;
    private final Map<EventMatcher, List<Action>> actionMapping;
    private final ExecutorService eventProcessor;
    private final List<Throwable> errors;
    private final Set<Object> received;
    private final Set<Object> sent;
    private transient CountDownLatch messageEvent;

    public MockPipesServer(String host, int port, boolean enableLogging, ProtocolCodecFactory protocolCodecFactory) {
        this.host = host;
        this.port = port;
        actionMapping = new ConcurrentHashMap<>();
        eventProcessor = Executors.newCachedThreadPool();
        errors = new ArrayList<>();
        received = new ConcurrentHashSet<>();
        sent = new ConcurrentHashSet<>();
        messageEvent = new CountDownLatch(0);

        socketAcceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder filterChain = socketAcceptor.getFilterChain();
        if (enableLogging) {
            filterChain.addFirst("logger", new LoggingFilter());
        }
        filterChain.addFirst("executor",
                new ExecutorFilter(Executors.newCachedThreadPool()));
        filterChain.addLast("codec", new ProtocolCodecFilter(protocolCodecFactory));
        // fix for OSX and to let tests be re-run without having to wait for a 30 second timeout
        socketAcceptor.setReuseAddress(true);
        socketAcceptor.setHandler(this);
    }

    private void dispatch(Event event) {
        LOG.debug("Processing event {}, message: {}", event, event.getMessage());
        LOG.debug("Actions: {}", actionMapping.size());
        actionMapping.forEach((eventType, actions) -> {
            if (eventType.matches(event)) {
                for (Action action : actions) {
                    LOG.debug("Matched mapping \"{}\" -> \"{}\"", eventType, action);
                    eventProcessor.submit(() -> {
                        try {
                            action.performOn(event);
                        } catch (RuntimeException e) {
                            errors.add(e);
                        }
                    });
                }
            }
        });
    }

    public synchronized void activate() {
        if (socketAcceptor.isDisposed() || socketAcceptor.isDisposing()) {
            throw new IllegalStateException("Activating a destroyed server is not allowed");
        }
        try {
            socketAcceptor.bind(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public synchronized void destroy() {
        LOG.debug("Shutting down...");
        socketAcceptor.unbind();
        socketAcceptor.dispose();
        eventProcessor.shutdown();
        LOG.debug("Shutdown finished.");
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOG.debug("Session opened [{}]", session.getId());
        Pipe pipe = new Pipe(session);
        session.setAttribute(Pipe.class, pipe);
        dispatch(new Event(Event.Type.CONNECTION_ESTABLISHED, pipe, null));
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        LOG.debug("Session closed [{}]", session.getId());
        Pipe pipe = (Pipe) session.getAttribute(Pipe.class);
        errors.addAll(pipe.getExpectedMessages().keySet().stream()
                .map(messageMatcher -> new RuntimeException("Expected messages not received: " + messageMatcher.toString()))
                .collect(Collectors.toList()));
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        // Race conditions can actually call messageReceived prior to sessionOpened
        Pipe pipe = null;
        while (pipe == null) {
            pipe = (Pipe) session.getAttribute(Pipe.class);
        }
        received.add(message);
        messageEvent.countDown();
        dispatch(new Event(Event.Type.MESSAGE_RECEIVED, pipe, message));
        pipe.onMessageReceived(message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // Race conditions can actually call messageSent prior to sessionOpened
        Pipe pipe = null;
        while (pipe == null) {
            pipe = (Pipe) session.getAttribute(Pipe.class);
        }
        sent.add(message);
        messageEvent.countDown();
        dispatch(new Event(Event.Type.MESSAGE_SENT, pipe, message));
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error(cause.getMessage(), cause);
        errors.add(cause);
    }

    @Override
    public AfterEventApi perform(Action... actions) {
        return eventType -> {
            List<Action> mappedActions = actionMapping.get(eventType);
            if (mappedActions == null) {
                mappedActions = new ArrayList<>();
                actionMapping.put(eventType, mappedActions);
            }
            mappedActions.addAll(Arrays.asList(actions));
        };
    }

    @Override
    public boolean waitForMessages(long timeout, MessageMatcher... messageMatchers) {
        long start = System.currentTimeMillis();
        Map<MessageMatcher, CountDownLatch> expectedMessages = new ConcurrentHashMap<>();
        for (MessageMatcher matcher : messageMatchers) {
            expectedMessages.put(matcher, new CountDownLatch(1));
        }
        AtomicBoolean done = new AtomicBoolean(false);
        eventProcessor.execute(() -> {
            while (expectedMessages.size() > 0 || !done.get()) {
                received.forEach(message -> expectedMessages.entrySet().removeIf(entry -> {
                    if (entry.getKey().matches(message)) {
                        entry.getValue().countDown();
                        return true;
                    } else {
                        return false;
                    }
                }));
                sent.forEach(message -> expectedMessages.entrySet().removeIf(entry -> {
                    if (entry.getKey().matches(message)) {
                        entry.getValue().countDown();
                        return true;
                    } else {
                        return false;
                    }
                }));
                messageEvent = new CountDownLatch(1);
                try {
                    messageEvent.await(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        try {
            for (CountDownLatch satisfied : expectedMessages.values()) {
                try {
                    if (!satisfied.await(timeout - System.currentTimeMillis() + start, TimeUnit.MILLISECONDS)) {
                        return false;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return true;
        } finally {
            done.set(true);
        }
    }

    @Override
    public List<Object> getReceived() {
        List<Object> received = new ArrayList<>(this.received);
        return received;
    }

    @Override
    public List<Object> getSent() {
        List<Object> sent = new ArrayList<>(this.sent);
        return sent;
    }

    public List<Throwable> getErrors() {
        return errors;
    }

}
