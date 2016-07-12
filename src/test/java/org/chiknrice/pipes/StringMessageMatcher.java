package org.chiknrice.pipes;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class StringMessageMatcher {

    public static MessageMatcher matchingString(String expected) {
        return new MessageMatcher() {

            private Pattern pattern = Pattern.compile(expected);

            @Override
            public boolean matches(Object message) {
                if (message instanceof String) {
                    return pattern.matcher((String) message).matches();
                } else {
                    return false;
                }
            }

            @Override
            public String toString() {
                return "Regex pattern \"" + expected + "\"";
            }
        };
    }

}
