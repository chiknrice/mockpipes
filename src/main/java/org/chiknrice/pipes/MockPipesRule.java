package org.chiknrice.pipes;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipesRule extends MockPipes, TestRule, MethodRule {
}
