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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
    public void simpleConversation() throws IOException, InterruptedException {
        try (Socket s = new Socket("localhost", 9999)) {
            s.setSoTimeout(3000);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                OutputStream out = s.getOutputStream();
                out.write("Lgoin\n".getBytes(StandardCharsets.ISO_8859_1));
                out.flush();
                if (!go.await(3000, TimeUnit.MILLISECONDS)) {
                    throw new RuntimeException("Not satisfied");
                }
                String reply = reader.readLine();
                assertThat(reply, is("Login"));
                out.write("Hello\nIan\n".getBytes(StandardCharsets.ISO_8859_1));
                out.flush();
                reply = reader.readLine();
                assertThat(reply, is("Nice!"));
            }
        }
    }

}
