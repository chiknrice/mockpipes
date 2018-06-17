package org.chiknrice.pipes.api;

/**
 * Configures events to trigger actions for every match for a specific pipe.
 *
 * @param <I>
 * @param <O>
 */
public interface MessageEventConfigurer<I, O> {

    /**
     * Configure an action after receiving a message matching {@code MessageMatcher}
     *
     * @param messageMatcher
     * @return
     */
    EventActionConfigurer<I, O, I, MessageEvent<I>> received(MessageMatcher<I> messageMatcher);

    /**
     * Configure an action after sending a message matching (@code MessageMatcher}
     *
     * @param messageMatcher
     * @return
     */
    EventActionConfigurer<I, O, O, MessageEvent<O>> sent(MessageMatcher<O> messageMatcher);

}
