package org.chiknrice.pipes.api;

/**
 * The {@code CustomAction} interface defines the API for performing a custom action based on a set of events
 *
 * @param <E>
 */
public interface CustomAction<E> {

    void perform(E... event);

}
