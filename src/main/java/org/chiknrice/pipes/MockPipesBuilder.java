package org.chiknrice.pipes;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * The {@code MockPipesBuilder} class is a builder for a {@code MockPipesServer} or a {@code MockPipesRule} which
 * provides methods to configure the {@code MockPipes}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class MockPipesBuilder {

    /**
     * The minimal configuration of a {@code MockPipes} is the port.
     *
     * @param port
     * @return
     */
    public static MockPipesBuilder configureWithPort(int port) {
        return new MockPipesBuilder(port);
    }

    private int port = 9999;
    private String host = "localhost";
    private MockPipesCodec<?> codec;
    private boolean enableLogging = false;

    private MockPipesBuilder(int port) {
        this.port = port;
    }

    /**
     * Sets the hostname or an ip where the {@code MockPipes} would listen to.
     *
     * @param host the hostname or ip of {@code MockPipes}
     * @return the builder
     */
    public MockPipesBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     * TODO make this the minimal required config and use ProtocolCodecFactory instead
     *
     * @param codec
     * @return
     */
    public MockPipesBuilder codec(MockPipesCodec<?> codec) {
        this.codec = codec;
        return this;
    }

    public MockPipesBuilder enableLogging() {
        this.enableLogging = true;
        return this;
    }

    public MockPipesServer buildServer() {
        return new MockPipesServer(host, port, enableLogging, codec);
    }

    public MockPipesRule buildRule() {
        return new MockPipesRuleImpl(buildServer());
    }

    private static class MockPipesRuleImpl implements MockPipesRule {

        final MockPipesServer server;

        MockPipesRuleImpl(MockPipesServer server) {
            this.server = server;
        }

        @Override
        public Statement apply(final Statement base, Description description) {
            return apply(base, null, null);
        }

        @Override
        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        server.activate();
                        base.evaluate();
                    } finally {
                        server.destroy();
                        MultipleFailureException.assertEmpty(server.getErrors());
                    }
                }
            };
        }

        @Override
        public AfterEventApi perform(Action... actions) {
            return server.perform(actions);
        }

        @Override
        public boolean waitForMessages(long timeout, MessageMatcher... messageMatchers) {
            return server.waitForMessages(timeout, messageMatchers);
        }

        @Override
        public List<Object> getReceived() {
            return server.getReceived();
        }

        @Override
        public List<Object> getSent() {
            return server.getSent();
        }

    }

}
