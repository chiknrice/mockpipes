package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;
import org.chiknrice.pipes.api.CustomAction;

import java.util.Set;

class DelegatingAction<E> implements Action<E> {

    private final CustomAction<E> delegate;

    DelegatingAction(CustomAction<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void perform(IoSession session, Set<E> trigger) {
        delegate.perform(trigger);
    }
}
