package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Events {

    public static EventMatcher messageReceived(MessageMatcher messageMatcher) {
        EventMatcher e =  event -> {
            if (event.getType().equals(Event.Type.MESSAGE_RECEIVED)) {
                return messageMatcher.matches(event.getMessage());
            } else {
                return false;
            }
        };
        return e.withToString("Message received matching: " + messageMatcher);
    }

    public static EventMatcher messageSent(MessageMatcher messageMatcher) {
        EventMatcher e = event -> {
            if (event.getType().equals(Event.Type.MESSAGE_SENT)) {
                return messageMatcher.matches(event.getMessage());
            } else {
                return false;
            }
        };
        return e.withToString("Message sent matching: " + messageMatcher);
    }

    public static EventMatcher connectionEstablished() {
        EventMatcher e = event -> event.getType().equals(Event.Type.CONNECTION_ESTABLISHED);
        return e.withToString("Connection established");
    }

    public static EventMatcher first(EventMatcher eventType) {
        return new MatchCountingEventType(eventType) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal == 1;
            }
        };
    }

    public static EventMatcher everyOdd(EventMatcher eventType) {
        return new MatchCountingEventType(eventType) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal % 2 == 1;
            }
        };
    }

}
