package org.chiknrice.pipes;

import java.util.List;

/**
 * The {@code MockPipes} interface provides the core APIs of a mock socket server.  The {@code MockPipes} is capable of
 * performing one or more {@code Action}s in reaction to a particular {@code Event}.  The API provi
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipes {

    /**
     * Perform a set of actions after a particular event.
     *
     * @param actions
     * @return
     */
    AfterEventApi perform(Action... actions);

    /**
     * Wait for messages (sent or received) to match in the given timeout.  This would be useful way of waiting for a
     * set of messages prior to start testing.
     *
     * @param timeout
     * @param messageMatchers
     * @return {@code true} if successful, or {@code false} if the specified waiting time elapses before messages were
     * sent/received
     */
    boolean waitForMessages(long timeout, MessageMatcher... messageMatchers);

    /**
     * Get all the messages received by the {@link MockPipesServer}.
     *
     * @return
     */
    List<Object> getReceived();

    /**
     * Get all the messages sent by the {@link MockPipesServer}
     *
     * @return
     */
    List<Object> getSent();

}
