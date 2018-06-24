package org.chiknrice.pipes;

import org.chiknrice.pipes.api.MockPipesMethodRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class MockPipesTest {

    @Rule
    public MockPipesMethodRule<String, String> server = MockPipesBuilder.withPort(9999).buildMethodRule(new StringCodec());

    private CountDownLatch go;

    @Before
    public void setupServer() {
        go = new CountDownLatch(2);
        server.afterConnected().expect(Message.of("Lgoin"), 2).then().send(Message.value("Login"));
        server.afterFirst().received(Message.matchingRegex("Ia.*")).send(Message.value("Nice!"));
        server.afterFirst().sent(Message.of("Login")).perform(e -> go.countDown());
        server.afterFirst().received(Message.of("Lgoin")).perform(e -> go.countDown());
        //server.afterFirst().received(Message.of("Hello")).raise(RuntimeException::new);
    }

    @Test
    public void simpleConversation() throws InterruptedException {
        BlockingQueue<String> responses = new ArrayBlockingQueue<>(5);
        Consumer<String> responseConsumer = response -> {
            try {
                responses.put(response);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        try (MockPipesClient<String, String> client = new MockPipesClient<>("localhost", 9999, new StringCodec(), responseConsumer)) {
            client.send("Lgoin");
            String reply = responses.poll(3000, TimeUnit.MILLISECONDS);
            assertThat(reply, is("Login"));
            client.send("Hello\nIan");
            reply = responses.poll(3000, TimeUnit.MILLISECONDS);
            assertThat(reply, is("Nice!"));
        }
    }

}
