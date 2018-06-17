package org.chiknrice.pipes.api;

/**
 * An message-related event
 *
 * @param <M> the message
 */
public interface MessageEvent<M> extends ConnectionEvent {

    M getMessage();

}
