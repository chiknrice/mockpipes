package org.chiknrice.pipes;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.apache.mina.util.ConcurrentHashSet;
import org.chiknrice.pipes.api.MockPipesCodec;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class MockPipesClient<I, O> extends IoHandlerAdapter implements Closeable {

    private final NioSocketConnector connector;
    private final IoSession session;
    private Consumer<I> responseConsumer;
    private AtomicLong connectionId = new AtomicLong(-1);
    private final Set<Exception> exceptions = new ConcurrentHashSet<>();

    public MockPipesClient(String host, int port, MockPipesCodec<I, O> codec, Consumer<I> responseConsumer) {
        this(host, port, codec, responseConsumer, 5000);
    }

    public MockPipesClient(String host, int port, MockPipesCodec<I, O> codec, Consumer<I> responseConsumer, long connectTimeout) {
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(connectTimeout);
//        connector.getFilterChain().addFirst("executor",
//                new ExecutorFilter(Executors.newCachedThreadPool()));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MockPipesCodecFactory<>(codec)));
//        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(this);
        this.responseConsumer = responseConsumer;
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();
    }

    @Override
    public void close() {
        // wait until the summation is done
        //session.getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

    public void send(O message) {
        session.write(message);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        if (responseConsumer != null) {
            try {
                responseConsumer.accept((I) message);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    public Set<Exception> getExceptions() {
        return exceptions;
    }

}
