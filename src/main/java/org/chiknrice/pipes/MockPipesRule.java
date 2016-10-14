package org.chiknrice.pipes;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;

/**
 * The {@code MockPipesRule} interface extends the {@code MockPipes} to function as a JUnit {@code TestRule} or {@code
 * MethodRule}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipesRule extends MockPipes, TestRule, MethodRule {
}
