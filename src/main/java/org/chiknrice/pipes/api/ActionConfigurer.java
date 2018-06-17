package org.chiknrice.pipes.api;

import java.util.function.Supplier;

/**
 * Configures actions in response to a single event
 *
 * @param <I> incoming message type
 * @param <O> outgoing message type
 * @param <E> event type
 */
public interface ActionConfigurer<I, O, E> {

    /**
     * Send out a message afterFirst an event
     *
     * @param messageBuilder
     */
    ChainActionConfigurer<I, O, E, MessageBuilder<O, E>> send(MessageBuilder<O, E> messageBuilder);

    /**
     * Expect a message afterFirst an event within the timeout
     *
     * @param messageMatcher
     * @param timeout
     * @return
     */
    ChainActionConfigurer<I, O, E, MessageMatcher<I>> expect(MessageMatcher<I> messageMatcher, long timeout);

    /**
     * Expect a message afterFirst an event within the default timeout (30 seconds)
     *
     * @param messageMatcher
     * @return
     */
    ChainActionConfigurer<I, O, E, MessageMatcher<I>> expect(MessageMatcher<I> messageMatcher);

    /**
     * Perform some custom action on the events
     *
     * @param action
     * @return
     */
    ChainActionConfigurer<I, O, E, MessageMatcher<I>> perform(CustomAction<E> action);

    /**
     * Raise exception afterFirst an event
     *
     * @param exceptionGenerator
     */
    void raise(Supplier<RuntimeException> exceptionGenerator);


}
