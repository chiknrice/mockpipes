package org.chiknrice.pipes;

/**
 * Class {@code Event} represents an event that happens on a particular pipe.  It can be any of the following types:
 * <pre>
 * - MESSAGE_RECEIVED
 * - MESSAGE_SENT
 * - CONNECTION_ESTABLISHED
 * </pre>
 * All events of a particular {@code Event.Type} is associated to a particular {@code Pipe}.  The {@code Event} contains
 * a reference to the {@code Pipe} where the event happened, the creation time, and optionally the message if the {@code
 * Event.Type} is either MESSAGE_RECEIVED or MESSAGE_SENT
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Event {

    public enum Type {MESSAGE_RECEIVED, MESSAGE_SENT, CONNECTION_ESTABLISHED}

    private final Type type;
    private final Pipe source;
    private final Object message;
    private final long creationTime;

    /**
     * Creates an event.
     *
     * @param type
     * @param source
     * @param message
     */
    Event(Type type, Pipe source, Object message) {
        if (type == null || source == null || (!Type.CONNECTION_ESTABLISHED.equals(type) && message == null)) {
            throw new NullPointerException("Required parameter is null");
        }
        this.type = type;
        this.source = source;
        this.message = message;
        this.creationTime = System.currentTimeMillis();
    }

    public Type getType() {
        return type;
    }

    public Pipe getSource() {
        return source;
    }

    public Object getMessage() {
        return message;
    }

    public long getElapsed() {
        return System.currentTimeMillis() - creationTime;
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
