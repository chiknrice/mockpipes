package org.chiknrice.pipes;

/**
 * The {@code EventMatcher} interface provides the API to encapsulate the logic of matching a particular {@code Event}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface EventMatcher {

    /**
     * Tries to match the provided {@code Event} using the underlying logic.
     *
     * @param event the {@code Event} to match
     * @return {@code true} if the {@code Event} matches, else {@code false}
     */
    boolean matches(Event event);

    default EventMatcher withToString(final String toString) {
        return new EventMatcher() {

            @Override
            public boolean matches(Event event) {
                return EventMatcher.this.matches(event);
            }

            @Override
            public String toString() {
                return toString;
            }

        };
    }

    default EventMatcher and(EventMatcher eventMatcher) {
        if (this instanceof InclusiveEventMatcher) {
            ((InclusiveEventMatcher) this).add(eventMatcher);
            return this;
        } else {
            InclusiveEventMatcher inclusiveEventMatcher = new InclusiveEventMatcher(this);
            inclusiveEventMatcher.add(eventMatcher);
            return inclusiveEventMatcher;
        }
    }

}
