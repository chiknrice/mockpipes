package org.chiknrice.pipes;

import org.chiknrice.pipes.api.MockPipes;
import org.chiknrice.pipes.api.MockPipesClassRule;
import org.chiknrice.pipes.api.MockPipesCodec;
import org.chiknrice.pipes.api.MockPipesMethodRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

import java.util.stream.Collectors;

/**
 * The {@code MockPipesBuilder} class is a builder for a {@code MockPipesServer} or a {@code MockPipesClassRule} which
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
    public static MockPipesBuilder withPort(int port) {
        return new MockPipesBuilder(port);
    }

    private final int port;
    private String host = "localhost";
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

    public MockPipesBuilder enableLogging() {
        this.enableLogging = true;
        return this;
    }

    public <I, O> MockPipes<I, O> buildServer(MockPipesCodec<I, O> codec) {
        return new MockPipesServer<>(host, port, enableLogging, codec);
    }

    public <I, O> MockPipesClassRule<I, O> buildClassRule(MockPipesCodec<I, O> codec) {
        return new MockPipesClassRuleImpl(host, port, enableLogging, codec);
    }

    public <I, O> MockPipesMethodRule<I, O> buildMethodRule(MockPipesCodec<I, O> codec) {
        return new MockPipesMethodRuleImpl(host, port, enableLogging, codec);
    }

    private static class MockPipesClassRuleImpl<I, O> extends MockPipesServer<I, O> implements MockPipesClassRule<I, O> {

        MockPipesClassRuleImpl(String host, int port, boolean enableLogging, MockPipesCodec<I, O> codec) {
            super(host, port, enableLogging, codec);
        }

        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        MockPipesClassRuleImpl.super.activate();
                        base.evaluate();
                    } finally {
                        MockPipesClassRuleImpl.super.destroy();
                    }
                }
            };
        }

        @Override
        public synchronized void activate() {
            throw new UnsupportedOperationException("activate not supported if used as a junit rule");
        }

        @Override
        public synchronized void destroy() {
            throw new UnsupportedOperationException("destroy not supported if used as a junit rule");
        }

    }

    private static class MockPipesMethodRuleImpl<I, O> extends MockPipesServer<I, O> implements MockPipesMethodRule<I, O> {

        public MockPipesMethodRuleImpl(String host, int port, boolean enableLogging, MockPipesCodec<I, O> mockPipesCodec) {
            super(host, port, enableLogging, mockPipesCodec);
        }

        @Override
        public Statement apply(Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        MockPipesMethodRuleImpl.super.activate();
                        base.evaluate();
                    } finally {
                        MockPipesMethodRuleImpl.super.destroy();
                        MultipleFailureException.assertEmpty(getExceptions()
                                .stream().map(Throwable.class::cast).collect(Collectors.toList()));
                    }
                }
            };
        }

        @Override
        public synchronized void activate() {
            throw new UnsupportedOperationException("activate not supported if used as a junit rule");
        }

        @Override
        public synchronized void destroy() {
            throw new UnsupportedOperationException("destroy not supported if used as a junit rule");
        }

    }

}
