package org.chiknrice.pipes.api;

/**
 * The {@code ConnectionEvent} provides a connection ID which the user can use to fetch the messages and exceptions
 * associated with the particular connection.  The {@code ConnectionEvent} is fired on first connection.
 */
public class ConnectionEvent {

    /**
     * Get the session ID of the socket which triggered the event
     *
     * @return
     */
    private final long connectionId;

    public ConnectionEvent(long connectionId) {
        this.connectionId = connectionId;
    }

    public long getConnectionId() {
        return connectionId;
    }

}
