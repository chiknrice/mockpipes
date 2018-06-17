package org.chiknrice.pipes;

import org.chiknrice.pipes.api.MessageMatcher;

/**
 * Sole purpose is to wrap the message matcher for uniqueness so it can be in a set while using the same message matcher
 * more than once
 *
 * @param <M>
 */
class UniqueMessageMatcher<M> implements MessageMatcher<M> {

    private final MessageMatcher<M> delegate;

    UniqueMessageMatcher(MessageMatcher<M> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean matches(M message) {
        return delegate.matches(message);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
