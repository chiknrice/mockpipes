package org.chiknrice.pipes.api;

/**
 * The {@code ConnectionEvent} provides a connection ID which the user can use to fetch the messages and exceptions
 * associated with the particular connection.  The {@code ConnectionEvent} is fired on first connection and would be
 * fired again on connection closed providing the same connection ID.
 */
public interface ConnectionEvent {

    /**
     * Get the session ID of the socket which triggered the event
     *
     * @return
     */
    long getConnectionId();

}
