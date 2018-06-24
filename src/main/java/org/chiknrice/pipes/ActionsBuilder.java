package org.chiknrice.pipes;

import org.chiknrice.pipes.api.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ActionsBuilder<I, O, E> implements ActionConfigurer<I, O, E>, EventActionsFactory<E> {

    protected final List<Action<E>> actions = new LinkedList<>();

    @Override
    public ChainActionConfigurer<I, O, E, MessageBuilder<O, E>> send(MessageBuilder<O, E> messageBuilder) {
        actions.add(new SendMessageAction<>(messageBuilder));
        return new ChainActionBuilder<>(another -> actions.add(new SendMessageAction<>(another)));
    }

    @Override
    public ChainActionConfigurer<I, O, E, MessageMatcher<I>> expect(MessageMatcher<I> messageMatcher, long timeout) {
        actions.add(new ExpectMessageAction<>(messageMatcher, timeout));
        return new ChainActionBuilder<>(another -> actions.add(new ExpectMessageAction<>(another, timeout)));
    }

    @Override
    public ChainActionConfigurer<I, O, E, MessageMatcher<I>> expect(MessageMatcher<I> messageMatcher) {
        actions.add(new ExpectMessageAction<>(messageMatcher, 10));
        return new ChainActionBuilder<>(another -> actions.add(new ExpectMessageAction<>(another, 10)));
    }

    @Override
    public ChainActionConfigurer<I, O, E, MessageMatcher<I>> perform(CustomAction<E> action) {
        actions.add(new DelegatingAction<>(action));
        return new ChainActionBuilder<>(another -> actions.add(new DelegatingAction<>(action)));
    }

    @Override
    public void raise(Supplier<RuntimeException> exceptionGenerator) {
        actions.add(new RaiseExceptionAction<>(exceptionGenerator));
    }

    @Override
    public EventActions<E> createInstance() {
        return (event, session) -> {
            // this only applies to ConnectionEvent as it is overridden for MessageEvent
            actions.forEach(action -> action.perform(session, Collections.singleton(event)));
            return true;
        };
    }

    class ChainActionBuilder<T> implements ChainActionConfigurer<I, O, E, T> {

        private final Consumer<T> actionConsumer;

        ChainActionBuilder(Consumer<T> actionConsumer) {
            this.actionConsumer = actionConsumer;
        }

        @Override
        public ChainActionConfigurer<I, O, E, T> and(T another) {
            actionConsumer.accept(another);
            return this;
        }

        @Override
        public ActionConfigurer<I, O, E> then() {
            return ActionsBuilder.this;
        }

    }

}
