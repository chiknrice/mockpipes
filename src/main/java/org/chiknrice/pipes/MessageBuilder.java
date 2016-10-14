package org.chiknrice.pipes;

/**
 * The {@code MessageBuilder} interface defines the API to build messages based on an {@code Event}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageBuilder {

    /**
     * Builds a message which is (optionally) based on an {@code Event}.
     *
     * @param event the {@code Event} which is associated to the instance when building a message
     * @return
     */
    Object build(Event event);

}
