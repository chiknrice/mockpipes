package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;
import org.chiknrice.pipes.api.MessageBuilder;

class SendMessageAction<M, E> implements Action<E> {

    private MessageBuilder<M, E> messageBuilder;

    SendMessageAction(MessageBuilder<M, E> messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public void perform(IoSession session, E... trigger) {
        try {

            Throwable exception = session.write(messageBuilder.build(trigger)).await().getException();
            if (exception != null) {
                throw new RuntimeException(exception.getMessage(), exception);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
