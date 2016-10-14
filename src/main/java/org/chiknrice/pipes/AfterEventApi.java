package org.chiknrice.pipes;

/**
 * The {@code AfterEventApi} interface defines the API to bind an action to a particular event type.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface AfterEventApi {

    /**
     * Binds the one or more underlying {@code Action}s to the {@code EventMatcher}
     *
     * @param eventType the {@code EventMatcher} instance
     */
    void after(EventMatcher eventType);

}
