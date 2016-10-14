package org.chiknrice.pipes;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code MatchCountingEventType} class is an {@code EventMatcher} implementation which counts the hits (i.e. the
 * number of times that an {@code Event} matches the wrapped {@code EventMatcher}) on a particular {@code Pipe} and
 * provides the option to the implementor to match based on the hits count.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public abstract class MatchCountingEventType implements EventMatcher {

    private final EventMatcher wrapped;

    private final WeakHashMap<Pipe, AtomicInteger> matchCount;

    public MatchCountingEventType(EventMatcher wrapped) {
        this.matchCount = new WeakHashMap<>();
        this.wrapped = wrapped;
    }

    @Override
    public boolean matches(Event event) {
        if (wrapped.matches(event)) {
            Pipe source = event.getSource();
            AtomicInteger count = matchCount.get(source);
            if (count == null) {
                count = new AtomicInteger(0);
                matchCount.put(source, count);
            }
            int matchOrdinal = count.incrementAndGet();
            return matchOnOrdinal(matchOrdinal);
        } else {
            return false;
        }
    }

    /**
     * Matches based on the ordinal passed
     *
     * @param ordinal the number of times the wrapped {@code EventMatcher} matches an event on a particular {@code
     *                Pipe}
     * @return {@code true} if the ordinal satisfies the required number of hits, else {@code false}
     */
    protected abstract boolean matchOnOrdinal(int ordinal);

}
