package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ConcurrentHashSet;
import org.chiknrice.pipes.api.MessageEvent;
import org.chiknrice.pipes.api.MessageMatcher;

import java.util.List;
import java.util.Set;

class MessageEventActions<M> implements EventActions<MessageEvent<M>> {

    private final Set<MessageMatcher<M>> messageMatchers;
    private final List<Action<MessageEvent<M>>> actions;
    private final boolean persistent;
    private final Set<MessageMatcher<M>> messageMatcherState;
    private final Set<MessageEvent<M>> matchedEvents;

    MessageEventActions(Set<MessageMatcher<M>> messageMatchers, List<Action<MessageEvent<M>>> actions, boolean persistent) {
        this.messageMatchers = messageMatchers;
        this.actions = actions;
        this.persistent = persistent;
        messageMatcherState = new ConcurrentHashSet<>(messageMatchers);
        matchedEvents = new ConcurrentHashSet<>();
    }

    @Override
    public boolean performActions(MessageEvent<M> event, IoSession session) {
        if (!messageMatcherState.isEmpty() && messageMatcherState.removeIf(matcher -> matcher.matches(event.getMessage()))) {
            matchedEvents.add(event);
            if (messageMatcherState.isEmpty()) {
                actions.forEach(action -> action.perform(session, matchedEvents.stream().toArray(MessageEvent[]::new)));
                // reset
                messageMatcherState.addAll(messageMatchers);
                matchedEvents.clear();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

}
