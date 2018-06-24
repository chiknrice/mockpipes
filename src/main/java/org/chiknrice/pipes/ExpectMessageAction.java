package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;
import org.chiknrice.pipes.api.MessageMatcher;

import java.util.Set;

class ExpectMessageAction<M, E> implements Action<E> {

    private MessageMatcher<M> messageMatcher;
    private long timeout;

    ExpectMessageAction(MessageMatcher<M> messageMatcher, long timeout) {
        this.messageMatcher = messageMatcher;
        this.timeout = timeout;
    }

    @Override
    public void perform(IoSession session, Set<E> trigger) {
        ExpectedMessage<M> expectedMessage = new ExpectedMessage<>(messageMatcher, timeout);
        IoSessionContext<M, ?> sessionContext = (IoSessionContext<M, ?>) session.getAttribute(IoSessionContext.class);
        sessionContext.addExpectedMessage(expectedMessage);
        expectedMessage.await();
    }
}
