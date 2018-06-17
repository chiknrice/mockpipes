package org.chiknrice.pipes;

import org.chiknrice.pipes.api.MessageBuilder;
import org.chiknrice.pipes.api.MessageMatcher;

import java.util.regex.Pattern;

public class Message {

    static MessageMatcher<String> of(String string) {
        return named(message -> message.equals(string), string);
    }

    static <E> MessageBuilder<String, E> value(String string) {
        return trigger -> string;
    }

    static MessageMatcher<String> matchingRegex(String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        return Message.<String>named(message -> pattern.matcher(message).matches(), "regex " + regexPattern);
    }

    static <M> MessageMatcher<M> named(MessageMatcher<M> matcher, String description) {
        return matcher.withDescription(description);
    }

}
