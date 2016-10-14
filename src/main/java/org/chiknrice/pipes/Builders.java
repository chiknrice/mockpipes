package org.chiknrice.pipes;

/**
 * The {@code Builders} class is a factory of different {@code MessageBuilder} instances.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Builders {

    /**
     * Creates a {@code MessageBuilder} which returns the same object parameter as the message.
     *
     * @param object the {@code Object} to return when {@code MessageBuilder#build} is called.
     * @return the new {@code MessageBuilder} instance
     */
    public static MessageBuilder objectMessage(Object object) {
        return event -> object;
    }

}
