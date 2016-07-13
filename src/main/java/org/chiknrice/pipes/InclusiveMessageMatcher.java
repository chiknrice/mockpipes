package org.chiknrice.pipes;

import org.apache.mina.util.ConcurrentHashSet;

import java.util.Set;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class InclusiveMessageMatcher<T> implements MessageMatcher<T> {

    private final Set<MessageMatcher<T>> matchers;

    public InclusiveMessageMatcher(MessageMatcher<T> matcher) {
        this.matchers = new ConcurrentHashSet<>();
        this.matchers.add(matcher);
    }

    public void add(MessageMatcher<T> matcher) {
        this.matchers.add(matcher);
    }

    @Override
    public boolean matches(T message) {
        boolean matches = true;
        for (MessageMatcher<T> matcher : matchers) {
            if (!matcher.matches(message)) {
                matches = false;
                break;
            }
        }
        return matches;
    }
}
