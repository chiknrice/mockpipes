package org.chiknrice.pipes;

/**
 * An API to bind an action to a particular event type.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface AfterEventApi {

    void after(EventMatcher eventType);

}
