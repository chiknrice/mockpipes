package org.chiknrice.pipes.api;

import org.junit.rules.TestRule;

/**
 * The {@code MockPipesClassRule} interface extends the {@code MockPipes} to function as a JUnit {@code TestRule} or {@code
 * MethodRule}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipesClassRule<I, O> extends MockPipes<I, O>, TestRule {
}
