package org.chiknrice.pipes;

import org.apache.mina.util.ConcurrentHashSet;

import java.util.Set;
import java.util.WeakHashMap;

/**
 * The {@code InclusiveEventMatcher} class consolidates one or more {@code EventMatcher}s and evaluates as matched once
 * all included {@code EventMatcher}s are satisfied.  This matcher only returns true once for a specific {@code Pipe}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class InclusiveEventMatcher implements EventMatcher {

    private final Set<EventMatcher> matchers;

    private final WeakHashMap<Pipe, Set<EventMatcher>> pipeScope;

    public InclusiveEventMatcher(EventMatcher matcher) {
        this.matchers = new ConcurrentHashSet<>();
        this.matchers.add(matcher);
        this.pipeScope = new WeakHashMap<>();
    }

    public void add(EventMatcher matcher) {
        matchers.add(matcher);
    }

    @Override
    public boolean matches(Event event) {
        Set<EventMatcher> pipeScopeEventMatchers = pipeScope.get(event.getSource());
        if (pipeScopeEventMatchers == null) {
            pipeScopeEventMatchers = new ConcurrentHashSet<>(matchers);
            pipeScope.put(event.getSource(), pipeScopeEventMatchers);
        }
        boolean matched = pipeScopeEventMatchers.removeIf(matcher -> matcher.matches(event));
        return matched && pipeScopeEventMatchers.size() == 0;
    }

}
