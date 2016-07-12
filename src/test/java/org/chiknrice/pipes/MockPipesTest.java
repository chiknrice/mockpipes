package org.chiknrice.pipes;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.chiknrice.pipes.Actions.expectMessage;
import static org.chiknrice.pipes.Actions.sendMessage;
import static org.chiknrice.pipes.Events.connectionEstablished;
import static org.chiknrice.pipes.Events.messageReceived;
import static org.chiknrice.pipes.Builders.objectMessage;
import static org.chiknrice.pipes.StringMessageMatcher.*;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class MockPipesTest {

    @Rule
    public MockPipesRule server = MockPipesBuilder.configureWithPort(9999).codec(new StringCodec()).buildRule();

    @Before
    public void setupServer() {
        server.du(expectMessage(matchingString("Lgoin"), 100)).on(connectionEstablished());
        server.du(sendMessage(objectMessage("Login"))).on(connectionEstablished());
        server.du(sendMessage(objectMessage("Nice!"))).on(messageReceived(matchingString("Ia.*")));
    }

    @Test
    public void testLogging() throws IOException {
        try (Socket s = new Socket("localhost", 9999)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                OutputStream out = s.getOutputStream();
                out.write("Lgoin\n".getBytes(StandardCharsets.ISO_8859_1));
                out.flush();
                if (!server.waitForMessages(1000, matchingString("Login"), matchingString("Lgoin"))) {
                    throw new RuntimeException("Not satisifed!!!");
                }
                System.out.println("Read: " + reader.readLine());
                out.write("Hello\nIan\n".getBytes(StandardCharsets.ISO_8859_1));
                out.flush();
                System.out.println("Read: " + reader.readLine());
            }
        }
    }

}
