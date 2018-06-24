package org.chiknrice.pipes.api;

import java.util.List;

/**
 * The {@code MockPipes} interface provides the core APIs to configure the behaviour of a mock socket server.  The
 * {@code MockPipes} is capable of performing one or more actions defined by {@code ActionConfigurer} in reaction to a
 * particular event.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipes<I, O> {

    ActionConfigurer<I, O, ConnectionEvent> afterConnected();

    /**
     * Provides a configurer for an event which binds an action to the first matching occurrence.
     *
     * @return
     */
    MessageEventConfigurer<I, O> afterFirst();

    /**
     * Provides a configurer for an event which binds an action to every matching occurrence.
     *
     * @return
     */
    MessageEventConfigurer<I, O> afterEvery();

    /**
     * Get all the messages received by {@link MockPipes}.
     *
     * @return the list
     */
    List<I> getReceived();

    /**
     * Get all the messages received by {@link MockPipes} from a specific client.
     *
     * @param connectionId
     * @return
     */
    List<I> getReceived(long connectionId);

    /**
     * Get all the messages sent by {@link MockPipes}.
     *
     * @return the list
     */
    List<O> getSent();

    /**
     * Get all the message sent by {@link MockPipes} to a specific client.
     * @param connectionId
     * @return
     */
    List<O> getSent(long connectionId);

    /**
     * Gets all the exceptions raised by {@link MockPipes} including caused by {@code raise} or {@code expect} actions.
     *
     * @return the list
     */
    List<Exception> getExceptions();

    /**
     * TODO:
     */
    void activate();

    /**
     * TODO:
     */
    void reset();

    /**
     * TODO:
     */
    void destroy();
}
