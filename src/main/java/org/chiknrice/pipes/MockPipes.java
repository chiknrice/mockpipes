package org.chiknrice.pipes;

import java.util.List;

/**
 * An API defining the possible operations that can be done by mock pipes
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipes {

    /**
     * Perform a specified action on a particular event.
     *
     * @param action
     * @return
     */
    OnEventApi du(Action action);

    /**
     * Wait for messages (sent or received) to match in the given timeout.  This would be useful way of waiting for a set of messages prior to start testing.
     *
     * @param timeout
     * @param messageMatchers
     * @return {@code true} if successful, or {@code false} if the specified waiting time elapses before messages were sent/received
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
