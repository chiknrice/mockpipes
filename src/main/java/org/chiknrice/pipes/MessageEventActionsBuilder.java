package org.chiknrice.pipes;

import org.chiknrice.pipes.api.EventActionConfigurer;
import org.chiknrice.pipes.api.MessageEvent;
import org.chiknrice.pipes.api.MessageEventConfigurer;
import org.chiknrice.pipes.api.MessageMatcher;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

class MessageEventActionsBuilder<I, O> implements MessageEventConfigurer<I, O> {

    private final boolean reusable;
    private final Consumer<EventActionsFactory<MessageEvent<I>>> receivedMessageActionsRegistry;
    private final Consumer<EventActionsFactory<MessageEvent<O>>> sentMessageActionsRegistry;

    MessageEventActionsBuilder(boolean reusable, Consumer<EventActionsFactory<MessageEvent<I>>> receivedMessageActionsRegistry, Consumer<EventActionsFactory<MessageEvent<O>>> sentMessageActionsRegistry) {
        this.reusable = reusable;
        this.receivedMessageActionsRegistry = receivedMessageActionsRegistry;
        this.sentMessageActionsRegistry = sentMessageActionsRegistry;
    }

    @Override
    public EventActionConfigurer<I, O, I, MessageEvent<I>> received(MessageMatcher<I> messageMatcher) {
        ActionsBuilderExt<I> actionsBuilder = new ActionsBuilderExt<>(messageMatcher);
        receivedMessageActionsRegistry.accept(actionsBuilder);
        return actionsBuilder;
    }

    @Override
    public EventActionConfigurer<I, O, O, MessageEvent<O>> sent(MessageMatcher<O> messageMatcher) {
        ActionsBuilderExt<O> actionsBuilder = new ActionsBuilderExt<>(messageMatcher);
        sentMessageActionsRegistry.accept(actionsBuilder);
        return actionsBuilder;
    }

    class ActionsBuilderExt<M> extends ActionsBuilder<I, O, MessageEvent<M>> implements EventActionConfigurer<I, O, M, MessageEvent<M>> {

        private final Set<MessageMatcher<M>> messageMatchers = new HashSet<>();

        ActionsBuilderExt(MessageMatcher<M> firstMessageMatcher) {
            messageMatchers.add(new UniqueMessageMatcher<>(firstMessageMatcher));
        }

        @Override
        public EventActionConfigurer<I, O, M, MessageEvent<M>> and(MessageMatcher<M> messageMatcher) {
            messageMatchers.add(new UniqueMessageMatcher<>(messageMatcher));
            return this;
        }

        @Override
        public EventActions<MessageEvent<M>> createInstance() {
            return new MessageEventActions<>(messageMatchers, actions, reusable);
        }
    }
}

