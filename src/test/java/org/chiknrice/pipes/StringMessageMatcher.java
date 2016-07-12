package org.chiknrice.pipes;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class StringMessageMatcher {

    public static MessageMatcher<String> matchingString(String expected) {
        return new MessageMatcher<String>() {

            private Pattern pattern = Pattern.compile(expected);

            @Override
            public boolean matches(String message) {
                return pattern.matcher(message).matches();
            }

            @Override
            public String toString() {
                return "Regex pattern \"" + expected + "\"";
            }
        };
    }

}
