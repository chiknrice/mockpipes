package org.chiknrice.pipes;

/**
 * An API to define ways of building messages based on an event
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MessageBuilder {

    Object build(Event event);

}
