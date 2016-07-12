package org.chiknrice.pipes;

import org.apache.mina.util.ConcurrentHashSet;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * An event matcher which matches once all included matchers are satisfied.  This matcher only returns true once for a
 * specific pipe.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class InclusiveEventMatcher implements EventMatcher {

    private final EventMatcher[] matchers;

    private final WeakHashMap<Pipe, Set<EventMatcher>> pipeScope;

    public InclusiveEventMatcher(EventMatcher[] matchers) {
        if (matchers.length == 0) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires at least 1 event matcher");
        }
        this.matchers = matchers;
        this.pipeScope = new WeakHashMap<>();
    }

    @Override
    public boolean matches(Event event) {
        Set<EventMatcher> eventMatchers = pipeScope.get(event.getSource());
        if (eventMatchers == null) {
            eventMatchers = new ConcurrentHashSet<>();
            Collections.addAll(eventMatchers, matchers);
            pipeScope.put(event.getSource(), eventMatchers);
        }
        boolean matched = eventMatchers.removeIf(matcher -> matcher.matches(event));
        return matched && eventMatchers.size() == 0;
    }

}
