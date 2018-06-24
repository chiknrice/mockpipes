package org.chiknrice.pipes.api;

/**
 * An message-related event TODO: elaborate
 *
 * @param <M> the message
 */
public class MessageEvent<M> extends ConnectionEvent {

    private final M message;

    public MessageEvent(long connectionId, M message) {
        super(connectionId);
        this.message = message;
    }

    public M getMessage() {
        return message;
    }

}
