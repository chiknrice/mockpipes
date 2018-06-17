package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;

import java.util.function.Supplier;

class RaiseExceptionAction<E> implements Action<E> {

    private final Supplier<RuntimeException> exceptionGenerator;

    RaiseExceptionAction(Supplier<RuntimeException> exceptionGenerator) {
        this.exceptionGenerator = exceptionGenerator;
    }

    @Override
    public void perform(IoSession session, E... trigger) {
        System.out.println("Throwing...");
        throw exceptionGenerator.get();
    }

}
