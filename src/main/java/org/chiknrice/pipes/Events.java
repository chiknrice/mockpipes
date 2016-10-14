package org.chiknrice.pipes;

/**
 * Class {@code Events} is a factory for {@code EventMatcher} instances
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Events {

    /**
     * Creates an {@code EventMatcher} which matches when a MESSAGED_RECEIVED event contains a message matching the
     * provided {@code MessageMatcher}
     *
     * @param messageMatcher that is used to match the received message
     * @return the {@code EventMatcher}
     */
    public static EventMatcher messageReceived(MessageMatcher messageMatcher) {
        EventMatcher e = event -> {
            if (Event.Type.MESSAGE_RECEIVED.equals(event.getType())) {
                return messageMatcher.matches(event.getMessage());
            } else {
                return false;
            }
        };
        return e.withToString("Message received matching: " + messageMatcher);
    }

    /**
     * Creates an {@code EventMatcher} which matches when a MESSAGED_SENT event contains a message matching the provided
     * {@code MessageMatcher}
     *
     * @param messageMatcher that is used to match the sent message
     * @return the {@code EventMatcher}
     */
    public static EventMatcher messageSent(MessageMatcher messageMatcher) {
        EventMatcher e = event -> {
            if (Event.Type.MESSAGE_SENT.equals(event.getType())) {
                return messageMatcher.matches(event.getMessage());
            } else {
                return false;
            }
        };
        return e.withToString("Message sent matching: " + messageMatcher);
    }

    /**
     * Creates an {@code EventMatcher} which matches when a CONNECTION_ESTABLISHED event occurs
     *
     * @return the {@code EventMatcher}
     */
    public static EventMatcher connectionEstablished() {
        EventMatcher e = event -> Event.Type.CONNECTION_ESTABLISHED.equals(event.getType());
        return e.withToString("Connection established");
    }

    /**
     * TODO
     * @param eventMatcher
     * @return the {@code EventMatcher}
     */
    public static EventMatcher first(EventMatcher eventMatcher) {
        return new MatchCountingEventType(eventMatcher) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal == 1;
            }
        };
    }

    /**
     * TODO
     * @param eventMatcher
     * @return {@code EventMatcher}
     */
    public static EventMatcher everyOdd(EventMatcher eventMatcher) {
        return new MatchCountingEventType(eventMatcher) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal % 2 == 1;
            }
        };
    }

}
