package org.chiknrice.pipes.api;

/**
 * The {@code MessageBuilder} interface defines the API to build messages based on an event.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageBuilder<M, E> {

    /**
     * Builds a message which is (optionally) based on an event or set of events.
     *
     * @param events the event
     * @return the message
     */
    M build(E... events);

}
