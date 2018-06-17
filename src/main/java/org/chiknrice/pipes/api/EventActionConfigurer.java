package org.chiknrice.pipes.api;

/**
 * An extension to an {@code ActionConfigurer} which allows for configuring additional events of the same type.
 *
 * @param <I> incoming message type
 * @param <O> outgoing message type
 * @param <M> message type required to be matched
 */
public interface EventActionConfigurer<I, O, M, E> extends ActionConfigurer<I, O, E> {

    /**
     * Define additional message matcher of the same message type prior to performing actions
     *
     * @param messageMatcher
     * @return
     */
    EventActionConfigurer<I, O, M, E> and(MessageMatcher<M> messageMatcher);

}
