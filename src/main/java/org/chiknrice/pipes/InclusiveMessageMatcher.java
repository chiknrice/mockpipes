package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class InclusiveMessageMatcher<T> implements MessageMatcher<T> {

    private final MessageMatcher<T>[] matchers;

    public InclusiveMessageMatcher(MessageMatcher<T>[] matchers) {
        this.matchers = matchers;
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
