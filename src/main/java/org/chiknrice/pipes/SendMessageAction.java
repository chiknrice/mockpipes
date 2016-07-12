package org.chiknrice.pipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class SendMessageAction implements Action {

    private static Logger LOG = LoggerFactory.getLogger(SendMessageAction.class);

    private MessageBuilder messageBuilder;

    public SendMessageAction(MessageBuilder messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void performOn(Event event) throws RuntimeException {
        event.getSource().send(messageBuilder.build(event));
    }

}
