package org.chiknrice.pipes;

import org.apache.mina.util.ConcurrentHashSet;
import org.chiknrice.pipes.api.MockPipesClassRule;
import org.chiknrice.pipes.api.MockPipesMethodRule;
import org.junit.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SimpleVolumeTest {

    @ClassRule
    public static MockPipesClassRule<String, String> server = MockPipesBuilder.withPort(9999).buildClassRule(new StringCodec());

    @Before
    public void setupServer() {
        server.afterConnected().send(trigger -> Long.toString(trigger.stream().findFirst().orElseThrow(RuntimeException::new).getConnectionId()));
        server.afterEvery().received(Message.any()).send(Message.echo());
    }

    @After
    public void resetServer() {
        // since the server is a class rule (shared between tests, the server needs to be reset after each and reconfigured
        server.reset();
    }

    @Test
    public void testThousandRequestsByTenClients() {
        performVolumeTest(10, 1000, 2);
    }

    @Test
    public void testHundredThousandRequestsByTwentyClients() {
        performVolumeTest(20, 100000, 10);
    }

    @Test
    @Ignore
    public void testMillionRequestsByThirtyClients() {
        performVolumeTest(30, 1000000, 120);
    }

    private void performVolumeTest(int totalClients, int totalRequests, int shouldFinishInSeconds) {
        long start = System.currentTimeMillis();
        CountDownLatch clientsReady = new CountDownLatch(totalClients);
        CountDownLatch responsesComplete = new CountDownLatch(totalRequests);
        Random random = new Random();
        ConcurrentHashMap<Integer, MockPipesClient<String, String>> clients = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Long> connectionIds = new ConcurrentHashMap<>();
        ConcurrentHashMap<Integer, Set<String>> clientResults = new ConcurrentHashMap<>();
        IntStream.range(0, totalClients).forEach(clientId -> {
            Set<String> responses = new ConcurrentHashSet<>();
            clientResults.put(clientId, responses);
            MockPipesClient<String, String> client = new MockPipesClient<>("localhost", 9999, new StringCodec(), response -> {
                if (clientsReady.getCount() > 0) {
                    connectionIds.put(clientId, Long.valueOf(response));
                    clientsReady.countDown();
                } else {
                    responses.add(response);
                    responsesComplete.countDown();
                }
            });
            clients.put(clientId, client);
        });

        try {
            clientsReady.await(5, TimeUnit.SECONDS);

            ConcurrentHashSet<Object> requests = new ConcurrentHashSet<>();
            IntStream.range(0, totalRequests).parallel().forEach(i -> {
                String request = Integer.toString(i);
                requests.add(request);
                int clientId = random.nextInt(totalClients);
                MockPipesClient<String, String> client = clients.get(clientId);
                client.send(request);
            });

            // assert everything is processed in expected time
            assertTrue(responsesComplete.await((shouldFinishInSeconds * 1000) - (System.currentTimeMillis() - start), TimeUnit.MILLISECONDS));
            // assert that no errors were encountered in the client's side
            clients.forEach((clientId,client) -> assertThat(client.getExceptions().size(), is(0)));
            // since this is an echo test, assert that received messages for a client is same count as responses for that client
            clientResults.forEach((clientId,responses) -> assertThat(server.getReceived(connectionIds.get(clientId)).size(), is(responses.size())));

            // again, since this is an echo test, assert that the combination of all responses are the same as all the requests
            Set<String> responses = clientResults.values().stream().flatMap(clientResponses -> clientResponses.stream()).collect(Collectors.toSet());
            assertThat(requests, is(responses));

            // asert that total sent messages by the server equals total requests plus the connectionsIds
            assertThat(server.getSent().size(), is(totalRequests + totalClients));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            clients.values().forEach(client -> {
                try {
                    client.close();
                } catch (Exception e) {
                    // ignore
                }
            });
        }
    }

}
