package org.chiknrice.pipes;

/**
 * Class {@code Actions} is a factory of different {@code Action} instances.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Actions {

    /**
     * Creates a send message action which would send a message using the provided {@link MessageBuilder}
     *
     * @param messageBuilder the builder used to create the message to be sent
     * @return the action
     */
    public static Action sendMessage(MessageBuilder messageBuilder) {
        return event -> event.getSource().send(messageBuilder.build(event));
    }

    /**
     * Expects a message to be received within the provided timeout matching the {@link MessageMatcher} provided
     *
     * @param messageMatcher the criteria of the message expected
     * @param timeout value in milliseconds
     * @return the action
     */
    public static Action expectMessage(MessageMatcher<?> messageMatcher, long timeout) {
        return event -> event.getSource().expect(messageMatcher, timeout);
    }

    /**
     * An action which raises an exception
     *
     * @param exception the exception to be thrown
     * @return the action
     */
    public static Action raise(RuntimeException exception) {
        return event -> {
            throw exception;
        };
    }

}
