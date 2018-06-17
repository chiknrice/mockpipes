package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;

interface Action<E> {

    void perform(IoSession session, E... trigger);

}
