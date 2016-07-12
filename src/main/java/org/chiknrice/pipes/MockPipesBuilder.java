package org.chiknrice.pipes;

import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.List;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class MockPipesBuilder {

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

    public MockPipesBuilder host(String host) {
        this.host = host;
        return this;
    }

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
        public AfterEventApi perform(Action action) {
            return server.perform(action);
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
