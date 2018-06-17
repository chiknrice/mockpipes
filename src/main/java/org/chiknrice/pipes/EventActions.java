package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;

interface EventActions<E> {

    /**
     * Execute the actions associated to the event if the event matches the implemented criteria
     *
     * @param event
     * @return <tt>true</tt> if the action did execute
     */
    boolean performActions(E event, IoSession session);

    /**
     * If the {@code EventActions} are persistent would always trigger actions each time the event matches
     *
     * @return
     */
    default boolean isPersistent() {
        return false;
    }

}
