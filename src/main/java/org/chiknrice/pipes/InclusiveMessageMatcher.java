package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class InclusiveMessageMatcher implements MessageMatcher {

    private final MessageMatcher[] matchers;

    public InclusiveMessageMatcher(MessageMatcher[] matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(Object message) {
        boolean matches = true;
        for (MessageMatcher matcher : matchers) {
            if (!matcher.matches(message)) {
                matches = false;
                break;
            }
        }
        return matches;
    }
}
