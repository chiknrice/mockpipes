package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Events {

    public static EventMatcher messageReceived(MessageMatcher messageMatcher) {
        EventMatcher e =  event -> {
            if (Event.Type.MESSAGE_RECEIVED.equals(event.getType())) {
                return messageMatcher.matches(event.getMessage());
            } else {
                return false;
            }
        };
        return e.withToString("Message received matching: " + messageMatcher);
    }

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

    public static EventMatcher connectionEstablished() {
        EventMatcher e = event -> Event.Type.CONNECTION_ESTABLISHED.equals(event.getType());
        return e.withToString("Connection established");
    }

    public static EventMatcher first(EventMatcher eventMatcher) {
        return new MatchCountingEventType(eventMatcher) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal == 1;
            }
        };
    }

    public static EventMatcher everyOdd(EventMatcher eventMatcher) {
        return new MatchCountingEventType(eventMatcher) {
            @Override
            protected boolean matchOnOrdinal(int ordinal) {
                return ordinal % 2 == 1;
            }
        };
    }

}
