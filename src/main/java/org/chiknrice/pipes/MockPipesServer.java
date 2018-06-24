package org.chiknrice.pipes;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.util.ConcurrentHashSet;
import org.chiknrice.pipes.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Server implementation
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
class MockPipesServer<I, O> extends IoHandlerAdapter implements MockPipes<I, O> {

    private static final Logger LOG = LoggerFactory.getLogger(MockPipesServer.class);

    // socket server config
    private final String host;
    private final int port;
    private final NioSocketAcceptor socketAcceptor = new NioSocketAcceptor();

    // collected messages per connection
    private final Map<Long, List<I>> received = new ConcurrentHashMap<>();
    private final Map<Long, List<O>> sent = new ConcurrentHashMap<>();

    // exceptions caught by the server
    private final Set<Throwable> exceptions = new ConcurrentHashSet<>();

    //private final DisruptorExecutor eventProcessor = new DisruptorExecutor("eventProcessor");
    //private final ExecutorService eventProcessor = Executors.newCachedThreadPool();
    private final ProducerSynchronizedExecutor<IoSession> eventProcessor = new ProducerSynchronizedExecutor<>();

    // server behavior config
    private final Set<EventActionsFactory<ConnectionEvent>> connectionEstablishedActions = new HashSet<>();
    private final Set<EventActionsFactory<MessageEvent<I>>> receivedMessageActions = new HashSet<>();
    private final Set<EventActionsFactory<MessageEvent<O>>> sentMessageActions = new HashSet<>();

    MockPipesServer(String host, int port, boolean enableLogging, MockPipesCodec<I, O> codec) {
        LOG.info("Creating instance...");
        this.host = host;
        this.port = port;
        DefaultIoFilterChainBuilder filterChain = socketAcceptor.getFilterChain();
        if (enableLogging) {
            filterChain.addFirst("logger", new LoggingFilter());
        }
        // let the NioProcessor thread do the publishing so codec is single thread per connection
//        filterChain.addFirst("executor", new ExecutorFilter(Executors.newCachedThreadPool()));
        filterChain.addLast("codec", new ProtocolCodecFilter(new MockPipesCodecFactory<>(codec)));
        socketAcceptor.setReuseAddress(true);
        socketAcceptor.setHandler(this);
    }

    @Override
    public synchronized void activate() {
        if (socketAcceptor.isDisposed() || socketAcceptor.isDisposing()) {
            throw new IllegalStateException("Activating a destroyed server is not allowed");
        }
        try {
            LOG.info("Binding to {}:{}...", host, port);
            socketAcceptor.bind(new InetSocketAddress(host, port));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void reset() {
        LOG.info("Resetting...");
        connectionEstablishedActions.clear();
        receivedMessageActions.clear();
        sentMessageActions.clear();
        received.clear();
        sent.clear();
        exceptions.clear();
    }

    @Override
    public synchronized void destroy() {
        LOG.info("Shutting down...");
        socketAcceptor.unbind();
        socketAcceptor.dispose();
    }

    @Override
    public void sessionCreated(IoSession session) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Session created [{}]", session.getId());
        }

        Set<EventActions<ConnectionEvent>> connectionEstablishedActions = this.connectionEstablishedActions
                .stream()
                .map(EventActionsFactory::createInstance)
                .collect(Collectors.toSet());

        Set<EventActions<MessageEvent<I>>> receivedMessageActions = this.receivedMessageActions
                .stream()
                .map(EventActionsFactory::createInstance)
                .collect(Collectors.toSet());

        Set<EventActions<MessageEvent<O>>> sentMessageActions = this.sentMessageActions
                .stream()
                .map(EventActionsFactory::createInstance)
                .collect(Collectors.toSet());

        IoSessionContext<I, O> ioSessionContext = new IoSessionContext(connectionEstablishedActions, receivedMessageActions, sentMessageActions);

        received.put(session.getId(), ioSessionContext.getReceivedMessages());
        sent.put(session.getId(), ioSessionContext.getSentMessages());

        session.setAttribute(IoSessionContext.class, ioSessionContext);

    }

    @Override
    public void sessionOpened(IoSession session) {
        ConnectionEvent event = new ConnectionEvent(session.getId());
        IoSessionContext<I, O> ioSessionContext = (IoSessionContext<I, O>) session.getAttribute(IoSessionContext.class);
        onEvent(event, session, ioSessionContext::getConnectionEstablishedActions,
                ioSessionContext::saveException);
    }

    @Override
    public void sessionClosed(IoSession session) {
        LOG.debug("Session closed [{}]", session.getId());
        IoSessionContext<I, O> context = getContext(session);
        context.getExceptions().stream().forEachOrdered(exceptions::add);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        MessageEvent<I> event = new MessageEvent<>(session.getId(), (I)message);
//        System.out.printf("[%s] - %s", Thread.currentThread().getName(), message);
        IoSessionContext<I, O> context = getContext(session);
        onMessageEvent(event, session, context::getReceivedMessageActions, context::saveReceived, context::saveException);
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        MessageEvent<O> event = new MessageEvent<>(session.getId(), (O)message);
        IoSessionContext<I, O> context = getContext(session);
        onMessageEvent(event, session, context::getSentMessageActions, context::saveSent, context::saveException);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        LOG.error(cause.getMessage(), cause);
        exceptions.add(new RuntimeException(cause.getMessage(), cause));
    }

    private IoSessionContext<I, O> getContext(IoSession session) {
        return (IoSessionContext<I, O>) session.getAttribute(IoSessionContext.class);
    }

    private <M> void onMessageEvent(MessageEvent<M> event, IoSession session,
                                    Supplier<Set<EventActions<MessageEvent<M>>>> eventActionsSupplier,
                                    Consumer<M> messageArchiver, Consumer<Exception> errorConsumer) {
        // save the message first (especially for received message so an expect action could be satisfied)
        messageArchiver.accept(event.getMessage());
        onEvent(event, session, eventActionsSupplier, errorConsumer);
    }

    private <E> void onEvent(E event, IoSession session,
                             Supplier<Set<EventActions<E>>> eventActionsSupplier,
                             Consumer<Exception> errorConsumer) {
        Set<EventActions<E>> eventActions = eventActionsSupplier.get();
        // eventProcessor is a producer-affinity ExecutorService which ensures all tasks associated with the same
        // IoSession is executed serially
        // , this is going to perform the actions of all NioProcessor threads
        eventActions.forEach(eventAction -> eventProcessor.submit(session, () -> {
            boolean actionsExecuted = false;
            try {
                actionsExecuted = eventAction.performActions(event, session);
            } catch (Exception e) {
                // actions are considered to have been run if exception was thrown
                // there's no distinction between exception thrown explicitly or exception occurred in a non exception action
                actionsExecuted = true;
                errorConsumer.accept(e);
            } finally {
                if (actionsExecuted && !eventAction.isPersistent()) {
                    eventActions.remove(eventAction);
                }
            }
        }));
    }

//    private <E> void dispatchToDisruptor(E event, IoSession session,
//                              Supplier<Set<EventActions<E>>> eventActionsSupplier,
//                              Consumer<Exception> errorConsumer) {
//        Set<EventActions<E>> eventActions = eventActionsSupplier.get();
//        eventActions.forEach(eventAction -> eventProcessor.submit(() -> {
//            boolean actionsExecuted = false;
//            try {
//                actionsExecuted = eventAction.performActions(event, session);
//            } catch (Exception e) {
//                // actions are considered to have been run if exception was thrown
//                // there's no distinction between exception thrown explicitly or
//                // exception occurred in a non exception action
//                actionsExecuted = true;
//                errorConsumer.accept(e);
//            } finally {
//                if (actionsExecuted && !eventAction.isPersistent()) {
//                    // these are just hashset - should probably be concurrent?
//                    eventActions.remove(eventAction);
//                }
//                eventProcessor.wakeUp();
//            }
//        }));
//    }

    @Override
    public ActionConfigurer<I, O, ConnectionEvent> afterConnected() {
        ActionsBuilder<I, O, ConnectionEvent> actionsBuilder = new ActionsBuilder<>();
        connectionEstablishedActions.add(actionsBuilder);
        return actionsBuilder;
    }

    @Override
    public MessageEventConfigurer<I, O> afterFirst() {
        return new MessageEventActionsBuilder<>(false, receivedMessageActions::add, sentMessageActions::add);
    }

    @Override
    public MessageEventConfigurer<I, O> afterEvery() {
        return new MessageEventActionsBuilder<>(true, receivedMessageActions::add, sentMessageActions::add);
    }

    @Override
    public List<I> getReceived() {
        return this.received.values().stream().flatMap(l -> l.stream()).collect(Collectors.toList());
    }

    @Override
    public List<I> getReceived(long connectionId) {
        return this.received.get(connectionId);
    }

    @Override
    public List<O> getSent() {
        return this.sent.values().stream().flatMap(l -> l.stream()).collect(Collectors.toList());
    }

    @Override
    public List<O> getSent(long connectionId) {
        return this.sent.get(connectionId);
    }

    @Override
    public List<Exception> getExceptions() {
        return exceptions
                .stream()
                .map(e -> e instanceof Exception ? Exception.class.cast(e) : new Exception(e.getMessage(), e))
                .collect(Collectors.toList());
    }

}
