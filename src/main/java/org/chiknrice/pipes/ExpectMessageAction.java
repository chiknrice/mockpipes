package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class ExpectMessageAction implements Action {

    private final MessageMatcher<?> messageMatcher;
    private final long timeout;

    public ExpectMessageAction(MessageMatcher<?> messageMatcher, long timeout) {
        this.messageMatcher = messageMatcher;
        this.timeout = timeout;
    }

    @Override
    public void performOn(Event event) throws RuntimeException {
        event.getSource().expect(messageMatcher, timeout);
    }

}
