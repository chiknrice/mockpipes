package org.chiknrice.pipes;

import org.apache.mina.core.session.IoSession;
import org.chiknrice.pipes.api.MessageEvent;
import org.chiknrice.pipes.api.MessageMatcher;

import java.util.*;

class MessageEventActions<M> implements EventActions<MessageEvent<M>> {

    private final List<Action<MessageEvent<M>>> actions;
    private final boolean persistent;
    private final MessageMatcher<M> singleMessageMatcher;
    private final Set<MessageMatcher<M>> multiMessageMatchers;
    private final Set<MessageMatcher<M>> multiMessageMatcherState;
    private final Set<MessageEvent<M>> multiEventsMatched;

    MessageEventActions(Set<MessageMatcher<M>> messageMatchers, List<Action<MessageEvent<M>>> actions, boolean persistent) {
        this.actions = actions;
        this.persistent = persistent;
        if (messageMatchers.size() == 1) {
            singleMessageMatcher = messageMatchers.iterator().next();
            multiMessageMatchers = null;
            multiMessageMatcherState = null;
            multiEventsMatched = null;
        } else {
            singleMessageMatcher = null;
            multiMessageMatchers = messageMatchers;
            multiMessageMatcherState = new TreeSet<>();
            multiEventsMatched = new TreeSet<>();
        }
    }

    // no need to synchronize here as all EventActions are performed by a single thread
    @Override
    public boolean performActions(MessageEvent<M> event, IoSession session) {
        if (singleMessageMatcher != null) {
            // TODO: maybe a ringbuffer won't require synchronization?
            if (singleMessageMatcher.matches(event.getMessage())) {
                actions.forEach(action -> action.perform(session, Collections.singleton(event)));
                return true;
            }
        } else if (multiMessageMatchers != null) {
            if (!multiMessageMatcherState.isEmpty() && multiMessageMatcherState.removeIf(matcher -> matcher.matches(event.getMessage()))) {
                multiEventsMatched.add(event);
                if (multiMessageMatcherState.isEmpty()) {
                    actions.forEach(action -> action.perform(session, multiEventsMatched));
                    // reset
                    multiMessageMatcherState.addAll(multiMessageMatchers);
                    multiEventsMatched.clear();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isPersistent() {
        return persistent;
    }

}
