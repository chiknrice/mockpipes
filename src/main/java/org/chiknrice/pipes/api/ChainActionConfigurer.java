package org.chiknrice.pipes.api;

/**
 * Configurer to allow for chaining further actions of similar type or another set of event-action mapping
 *
 * @param <I>
 * @param <O>
 * @param <E>
 * @param <T>
 */
public interface ChainActionConfigurer<I, O, E, T> {

    ChainActionConfigurer<I, O, E, T> and(T another);

    ActionConfigurer<I, O, E> then();

}
