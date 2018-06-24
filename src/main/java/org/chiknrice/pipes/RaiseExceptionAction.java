package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;

import java.util.Set;
import java.util.function.Supplier;

class RaiseExceptionAction<E> implements Action<E> {

    private final Supplier<RuntimeException> exceptionGenerator;

    RaiseExceptionAction(Supplier<RuntimeException> exceptionGenerator) {
        this.exceptionGenerator = exceptionGenerator;
    }

    @Override
    public void perform(IoSession session, Set<E> trigger) {
        throw exceptionGenerator.get();
    }

}
