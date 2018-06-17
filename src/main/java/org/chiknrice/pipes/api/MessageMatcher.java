package org.chiknrice.pipes.api;

/**
 * The {@code MessageMatcher} interface provides the API to define a criteria of matching messages.  The implementation
 * is recommended to override the toString() method to print what kind of message matching is done to aid in composing
 * exception messages.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageMatcher<M> {

    boolean matches(M message);

    /**
     * Set the description which is used for the message matcher's toString()
     *
     * @param description
     * @return
     */
    default MessageMatcher<M> withDescription(final String description) {
        return new MessageMatcher<M>() {

            @Override
            public boolean matches(M event) {
                return MessageMatcher.this.matches(event);
            }

            @Override
            public String toString() {
                return description;
            }

        };
    }

}
