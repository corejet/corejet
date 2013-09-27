package org.corejet.testrunner;

import org.corejet.repository.StoryRepository;
import org.junit.runners.model.InitializationError;

/**
 * Selenium specific implementation to add screen shot links for failed tests by switching the framework method
 * @author rpickard
 *
 */
public class CoreJetSeleniumTestRunner extends CoreJetTestRunner{

	public CoreJetSeleniumTestRunner(Class<?> testClass, StoryRepository storyRepository) throws CorejetException,InitializationError, SurplusScenarioException {
		super(testClass, storyRepository);
	}
	
	public CoreJetSeleniumTestRunner(Class<?> testClass) throws CorejetException, InitializationError, SurplusScenarioException {
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
