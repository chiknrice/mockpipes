package org.chiknrice.pipes;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A qualifier to determine when an event should trigger an action.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public abstract class MatchCountingEventType implements EventMatcher {

    private final EventMatcher eventType;

    private final WeakHashMap<Pipe, AtomicInteger> matchCount;

    public MatchCountingEventType(EventMatcher eventType) {
        this.matchCount = new WeakHashMap<>();
        this.eventType = eventType;
    }

    @Override
    public boolean matches(Event event) {
        boolean matches = eventType.matches(event);
        if (matches) {
            Pipe source = event.getSource();
            AtomicInteger count = matchCount.get(source);
            if (count == null) {
                count = new AtomicInteger(0);
                matchCount.put(source, count);
            }
            int matchOrdinal = count.incrementAndGet();
            return matchOnOrdinal(matchOrdinal);
        }
        return eventType.matches(event);
    }

    protected abstract boolean matchOnOrdinal(int ordinal);

}
