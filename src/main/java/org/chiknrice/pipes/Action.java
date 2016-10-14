package org.chiknrice.pipes;

/**
 * The {@code Action} interface defines the API to encapsulate logic that would be performed after a particular {@code
 * Event}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface Action {

    /**
     * Performs a logic which (optionally) is based on the {@code Event}.  Any checked exceptions should be wrapped by
     * the implementor with a {@code RuntimeException}
     *
     * @param event the {@code Event} which triggered the {@code Action}
     */
    void performOn(Event event);

}
