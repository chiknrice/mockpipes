package org.chiknrice.pipes.api;

import org.junit.rules.MethodRule;

public interface MockPipesMethodRule<I, O> extends MockPipes<I, O>, MethodRule {
}
