package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Event {

    public enum Type {MESSAGE_RECEIVED, MESSAGE_SENT, CONNECTION_ESTABLISHED}

    private final Type type;
    private final Pipe source;
    private final Object message;
    private final long creationTime;

    public Event(Type type, Pipe source) {
        this(type, source, null);
    }

    public Event(Type type, Pipe source, Object message) {
        if (type == null || source == null) {
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
