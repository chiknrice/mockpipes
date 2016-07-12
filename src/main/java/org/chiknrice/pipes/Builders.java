package org.chiknrice.pipes;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class Builders {

    public static MessageBuilder objectMessage(Object string) {
        return event -> string;
    }

}
