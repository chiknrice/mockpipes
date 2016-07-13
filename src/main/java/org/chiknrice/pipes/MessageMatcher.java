package org.chiknrice.pipes;

/**
 * An API to define a criteria of matching messages.  The implementation is recommended to override the toString()
 * method to print what kind of message matching is done to aid in composing exception messages.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageMatcher<T> {

    boolean matches(T message);

    default MessageMatcher<T> and(MessageMatcher<T> matcher) {
        if (this instanceof InclusiveEventMatcher) {
            ((InclusiveMessageMatcher<T>) this).add(matcher);
            return this;
        } else {
            InclusiveMessageMatcher inclusiveMessageMatcher = new InclusiveMessageMatcher<>(this);
            inclusiveMessageMatcher.add(matcher);
            return inclusiveMessageMatcher;
        }
    }

}
