package org.chiknrice.pipes;

interface EventActionsFactory<E> {

    EventActions<E> createInstance();

}
