package org.corejet.testrunner;

import org.junit.runners.model.InitializationError;

/**
 * Selenium specific implementation to add screen shot links for failed tests by switching the framework method
 * @author rpickard
 *
 */
public class ParallelCoreJetSeleniumTestRunner extends ParallelCoreJetTestRunner {
	
	public ParallelCoreJetSeleniumTestRunner(Class<?> testClass) throws CorejetException, InitializationError, SurplusScenarioException {
		super(testClass);
	}

	@Override
	protected CoreJetFrameworkMethod createFrameworkMethod(org.corejet.model.Scenario scenario, Class<?> scenarioInnerClass) {
		CoreJetFrameworkMethod coreJetFrameworkMethod;
		try {
			coreJetFrameworkMethod = new CoreJetSeleniumFrameworkMethod(scenarioInnerClass, scenario);
		} catch (Exception e) {
			throw new CorejetException("Failed to create corejet framework method", e);
		}
		return coreJetFrameworkMethod;
	}

}
