package org.chiknrice.pipes;

/**
 * An API to encapsulate the logic on when an event should trigger an action.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface EventMatcher {

    /**
     * @param event
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
