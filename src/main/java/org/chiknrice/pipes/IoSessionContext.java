package org.chiknrice.pipes;

import org.chiknrice.pipes.api.ConnectionEvent;
import org.chiknrice.pipes.api.MessageEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class IoSessionContext<I, O> {

    private final List<Throwable> exceptions = new ArrayList<>();
    private final List<I> receivedMessages = new ArrayList<>();
    private final List<O> sentMessages = new ArrayList<>();
    private final Set<ExpectedMessage<I>> expectedMessages = new HashSet<>();
    private final Set<EventActions<ConnectionEvent>> connectionEstablishedActions;
    private final Set<EventActions<MessageEvent<I>>> receivedMessageActions;
    private final Set<EventActions<MessageEvent<O>>> sentMessageActions;

    IoSessionContext(Set<EventActions<ConnectionEvent>> connectionEstablishedActions, Set<EventActions<MessageEvent<I>>> receivedMessageActions, Set<EventActions<MessageEvent<O>>> sentMessageActions) {
        this.connectionEstablishedActions = connectionEstablishedActions;
        this.receivedMessageActions = receivedMessageActions;
        this.sentMessageActions = sentMessageActions;
    }

    void saveReceived(I message) {
        expectedMessages.removeIf(expected -> expected.matches(message));
        receivedMessages.add(message);
    }

    List<I> getReceivedMessages() {
        return receivedMessages;
    }

    void saveSent(O message) {
        sentMessages.add(message);
    }

    List<O> getSentMessages() {
        return sentMessages;
    }

    void saveException(Exception exception) {
        exceptions.add(exception);
    }

    List<Throwable> getExceptions() {
        return exceptions;
    }

    void addExpectedMessage(ExpectedMessage<I> expected) {
        if (!receivedMessages.stream().anyMatch(expected::matches)) {
            expectedMessages.add(expected);
        }
    }

    Set<EventActions<ConnectionEvent>> getConnectionEstablishedActions() {
        return connectionEstablishedActions;
    }

    Set<EventActions<MessageEvent<I>>> getReceivedMessageActions() {
        return receivedMessageActions;
    }

    Set<EventActions<MessageEvent<O>>> getSentMessageActions() {
        return sentMessageActions;
    }
}
