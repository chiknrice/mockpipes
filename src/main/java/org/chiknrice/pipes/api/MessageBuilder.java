package org.chiknrice.pipes.api;

import java.util.Set;

/**
 * The {@code MessageBuilder} interface defines the API to build messages based on a set of events.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageBuilder<M, E> {

    /**
     * Builds a message which is (optionally) based on an event or set of events.
     *
     * @param trigger the set of events which triggered the action
     * @return the message
     */
    M build(Set<E> trigger);

}
