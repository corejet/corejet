package org.corejet.testrunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.corejet.annotations.Defect;
import org.corejet.annotations.Given;
import org.corejet.annotations.NotAutomatable;
import org.corejet.annotations.NotImplementedYet;
import org.corejet.annotations.Scenario;
import org.corejet.annotations.Story;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.AwaitingFunctionality;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.ScenarioStatus;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * JUnit test runner which executes corejet-structured Story/Scenario classes.
 * 
 * @author rnorth
 *
 */
public class CoreJetTestRunner extends BlockJUnit4ClassRunner {

	private Description fixtureDescription;
	private Map<FrameworkMethod, Description> standinMethods = Maps.newHashMap();
	private CoreJetXmlWritingRunListener runListener = null;
	private static CoreJetXmlWritingRunListener attatchedRunListener = null;
	private boolean storyInRepository;
	protected StoryRepository storyRepository;
	private static final Logger logger = LoggerFactory.getLogger(CoreJetTestRunner.class);

	private static final String FILTER_REGEX = "corejet.filter";

	/**
	 * @param testClass
	 * @throws InitializationError
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws StoryRepositoryException 
	 * @throws SurplusScenarioException 
	 */
	public CoreJetTestRunner(Class<?> testClass) throws CorejetException, InitializationError, SurplusScenarioException {
		super(testClass);
		init(testClass);
	}

	public CoreJetTestRunner(Class<?> testClass, StoryRepository storyRepository) throws CorejetException, InitializationError, SurplusScenarioException {
		super(testClass);
		this.storyRepository = storyRepository;
		init(testClass);
	}

	private void init(Class<?> testClass) {
		Story storyAnnotation = testClass.getAnnotation(Story.class);
		String storyId = storyAnnotation.id();
		String storyTitle = storyAnnotation.title();

		if (null==storyRepository){
			StorySource storySourceAnnotation = testClass.getAnnotation(StorySource.class);
			if (storySourceAnnotation == null) {
				throw new CoreJetJunitTestRunnerException("No @StorySource annotation found on Story test class " + testClass);
			}
			Class<? extends StoryRepository> storyRepositoryClass = storySourceAnnotation.value();
			try {
				storyRepository = (StoryRepository) storyRepositoryClass.newInstance();
			} catch (InstantiationException e) {
				throw new CorejetException("Could not instantiate required story repository!", e);
			} catch (IllegalAccessException e) {
				throw new CorejetException("Could not instantiate required story repository!", e);
			}

		}

		fixtureDescription = Description.createTestDescription(testClass, "["+storyId+"] " + storyTitle);

		org.corejet.model.Story story;
		try {
			story = storyRepository.getAllStories().get(storyId);

			if (null==story){
				//throw new StoryRepositoryException("No story with id "+storyId+" was found");
				storyInRepository=false;
				return;
			} else {
				storyInRepository=true;
			}
		} catch (StoryRepositoryException e) {
			throw new CorejetException("Could not get story with ID " + storyId + " from story repository " + storyRepository, e);
		}

		if (!story.getTitle().equals(storyTitle)){
			story.setRequirementResolution(ScenarioStatus.MISMATCH.getName());
		} 

		for (org.corejet.model.Scenario scenario: story.getScenarios()) {

			try {
				Class<?> scenarioInnerClass = getScenarioInnerClassFor(scenario, testClass);

				// Don't run the test if annotated @NotImplementedYet, @WaitingForFunctionality, or @Defect		

				String regex = System.getProperty(FILTER_REGEX);
				if (scenarioInnerClass.isAnnotationPresent(NotImplementedYet.class)){
					scenario.setStatus(ScenarioStatus.TODO);
				} else if (scenarioInnerClass.isAnnotationPresent(AwaitingFunctionality.class) )  {
					scenario.setStatus(ScenarioStatus.PENDING);
				} else if (scenarioInnerClass.isAnnotationPresent(NotAutomatable.class) )  {
					scenario.setStatus(ScenarioStatus.NA);
				}else if (scenarioInnerClass.isAnnotationPresent(Defect.class) )  {
					scenario.setStatus(ScenarioStatus.DEFECT);
					scenario.setDefect(scenarioInnerClass.getAnnotation(Defect.class).value());
				}else if (null!=regex && !scenario.getName().matches(regex)){
					// if a regex filter is provided, skip the scenarios that don't match the filter
					scenario.setStatus(ScenarioStatus.TODO);
				} else {
					CoreJetFrameworkMethod coreJetFrameworkMethod;
					try {
						coreJetFrameworkMethod = new CoreJetFrameworkMethod(scenarioInnerClass, scenario);
					} catch (SecurityException e) {
						throw new CorejetException("Failed to create corejet framework method", e);
					} catch (NoSuchMethodException e) {
						throw new CorejetException("Failed to create corejet framework method", e);
					}
					Description scenarioDescription = Description.createTestDescription(scenarioInnerClass, scenario.getName());
					fixtureDescription.addChild(scenarioDescription);

					standinMethods.put(coreJetFrameworkMethod, scenarioDescription);
				} 
			} catch (CoreJetJunitTestRunnerException e){
				// This is thrown when a match is not found, status is pending
			} 
		}

		// Identify any extra scenarios or steps
		Class<?>[] declaredClasses = testClass.getDeclaredClasses();
		for(Class<?> c : declaredClasses) {

			Scenario innerClassScenarioAnnotation = c.getAnnotation(Scenario.class);
			if (innerClassScenarioAnnotation != null) {
				String scenarioName = innerClassScenarioAnnotation.value();

				if (!story.hasScenarioNamed(scenarioName)) {
					org.corejet.model.Scenario extra = new org.corejet.model.Scenario();
					extra.setName(scenarioName);
					extra.setStatus(ScenarioStatus.SUPERFLUOUS);
					for (Method m : c.getMethods()){
						if (null!=m.getAnnotation(Given.class)){
							extra.getGivens().add(m.getAnnotation(Given.class).value());
						} else if (null!=m.getAnnotation(When.class)){
							extra.getWhens().add(m.getAnnotation(When.class).value());
						} else if (null!=m.getAnnotation(Then.class)){
							extra.getThens().add(m.getAnnotation(Then.class).value());
						}
					}
					story.getScenarios().add(extra );
				} 
			}
		}
		RequirementsCatalogue requirementsCatalogue;
		try {
			requirementsCatalogue = storyRepository.getRequirementsCatalogue();
		} catch (StoryRepositoryException e) {
			throw new CorejetException("Could not get requirements catalogue", e);
		}

		try {
			runListener = new CoreJetXmlWritingRunListener(requirementsCatalogue, story);
		} catch (Exception e) {
			throw new CorejetException("Failed to create CoreJetXmlWritingRunListener", e);
		}
	}

	/**
	 * @param scenario
	 * @param testClass
	 * @return
	 */
	private Class<?> getScenarioInnerClassFor(org.corejet.model.Scenario scenario, Class<?> testClass) {
		Class<?>[] declaredClasses = testClass.getDeclaredClasses();
		for(Class<?> c : declaredClasses) {

			Scenario innerClassScenarioAnnotation = c.getAnnotation(Scenario.class);
			if (innerClassScenarioAnnotation != null && innerClassScenarioAnnotation.value().equals(scenario.getName())) {
				return c;
			}
		}

		throw new CoreJetJunitTestRunnerException("Could not locate matching inner Scenario class for defined scenario '" + scenario.getName() + "' within story: " + scenario.getParentStory().getId());
	}

	/**
	 * @param method
	 * @return
	 */
	private Statement testCaseExecStatement(CoreJetFrameworkMethod method) {

		runListener.preTestMethodInvocation(method);

		Statement result = method.invoke();

		return result;
	}

	/**  
	 * @{inheritDoc}
	 */
	public Description getDescription() {
		return fixtureDescription;
	}

	/**  
	 * @{inheritDoc}
	 */
	@Override
	protected List<FrameworkMethod> getChildren() {

		List<FrameworkMethod> children = new ArrayList<FrameworkMethod>();
		children.addAll(standinMethods.keySet());
		children.addAll(super.getChildren());
		return children;
	}

	/**  
	 * @{inheritDoc}
	 */
	@Override
	protected Statement methodInvoker(FrameworkMethod method, Object test) {
		if (standinMethods.keySet().contains(method)
				&& method instanceof CoreJetFrameworkMethod) {
			CoreJetFrameworkMethod coreJetFrameworkMethod = (CoreJetFrameworkMethod) method;

			coreJetFrameworkMethod.setParentInstance(test);

			return testCaseExecStatement(coreJetFrameworkMethod);
		}
		return super.methodInvoker(method, test);
	}

	/**  
	 * @{inheritDoc}
	 */
	protected Description describeChild(FrameworkMethod method) {
		if (standinMethods.keySet().contains(method)) {
			return (Description) standinMethods.get(method);
		}
		return super.describeChild(method);
	}

	/**  
	 * @{inheritDoc}
	 */
	protected void validateInstanceMethods(List<Throwable> errors) {
		validatePublicVoidNoArgMethods(After.class, false, errors);
		validatePublicVoidNoArgMethods(Before.class, false, errors);
		validateTestMethods(errors);
	}

	/**  
	 * @{inheritDoc}
	 */
	public void run(RunNotifier notifier) {

		// Don't run stories if they are not in the repository
		if (storyInRepository){
			// RunListener may have been added already to this notifier, but we don't know
			notifier.removeListener(attatchedRunListener);
			notifier.addListener(runListener);
			logger.debug("Adding listener: " + runListener + " to notifier: " + notifier);
			attatchedRunListener = runListener;
			super.run(notifier);
			runListener.writeStoryResult();
		} else {
			logger.info("*** Skipping story that was not in the repository");
		}

	}

}
