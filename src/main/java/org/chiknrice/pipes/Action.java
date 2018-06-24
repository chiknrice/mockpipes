package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;

import java.util.Set;

interface Action<E> {

    void perform(IoSession session, Set<E> trigger);

}
