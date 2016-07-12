package org.chiknrice.pipes;

/**
 * An API to encapsulate an action that is performed after an event happens.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface Action {

    void performOn(Event event) throws RuntimeException;

}
