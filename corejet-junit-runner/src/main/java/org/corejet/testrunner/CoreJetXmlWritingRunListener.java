package org.corejet.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.corejet.Configuration;
import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Scenario;
import org.corejet.model.ScenarioStatus;
import org.corejet.model.Story;
import org.corejet.model.WritingException;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit run listener which generates a CoreJet XML requirements catalogue file,
 * augmented with the results of story tests.
 * 
 * @author rnorth
 * 
 */
class CoreJetXmlWritingRunListener extends RunListener {
	
	private File corejetReportOutputFile = null;

	private static final Logger testProgressLogger = LoggerFactory.getLogger("corejet");
	
	private static final Random random = new Random(new Date().getTime());

	private RequirementsCatalogue requirementsCatalogue;

	private Story story;

	private static Map<String, CoreJetFrameworkMethod> classToFrameworkMethodMap = new ConcurrentHashMap<String, CoreJetFrameworkMethod>();

	/**
	 * @param requirementsCatalogue
	 */
	public CoreJetXmlWritingRunListener(RequirementsCatalogue requirementsCatalogue, Story story) throws Exception {

		this.story = story;
		this.requirementsCatalogue = requirementsCatalogue;
		requirementsCatalogue.setTestTime(new Date());

		setAllRequirementsAsToDo();

		// create the base file if it doesn't exist
		File corejetReportRepositoryFile = new File(Configuration.getBaseDirectory()+Configuration.BASE_REPORT_PREFIX + random.nextLong() + ".xml");
		if(!corejetReportRepositoryFile.exists()){
			corejetReportRepositoryFile.getParentFile().mkdirs();
			OutputStream outputStream = new FileOutputStream(corejetReportRepositoryFile);
			requirementsCatalogue.write(outputStream);
			outputStream.flush();
		}


	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void testFinished(Description description) throws Exception {
		CoreJetFrameworkMethod frameworkMethod = classToFrameworkMethodMap.get(description.getClassName());
		// Don't intercept non-corejet test results
		if (null!=frameworkMethod) {
			Scenario scenario = frameworkMethod.getScenario();
			// Don't overwrite a fail we've just been notified of
			if (scenario.getStatus().equals(ScenarioStatus.TODO)) {
				scenario.setStatus(ScenarioStatus.PASS);
			}
		}
		super.testFinished(description);
	}



	/**
	 * @{inheritDoc
	 */
	@Override
	public void testFailure(Failure failure) throws Exception {
		testProgressLogger.error("**** Test failure: [" + failure.getDescription().getMethodName() + "]\t Exception: ["
				+ getThrowableCause(failure.getException()) + "]");

		String testClass = failure.getDescription().getClassName();

		CoreJetFrameworkMethod frameworkMethod = classToFrameworkMethodMap.get(testClass);
		frameworkMethod.getScenario().setStatus(ScenarioStatus.FAIL);

		if(failure.getException() instanceof SurplusScenarioException){
			frameworkMethod.getScenario().setStatus(ScenarioStatus.SUPERFLUOUS);
		} else if (failure.getException() instanceof CoreJetJunitTestRunnerException){
			frameworkMethod.getScenario().setStatus(ScenarioStatus.MISMATCH);
		}
	}

	/**
	 * Write the results file for the story
	 * @throws WritingException
	 * @throws IOException
	 */
	public void writeStoryResult() {
		if (null!=story){
			try {
				corejetReportOutputFile = new File(Configuration.getBaseDirectory()+"/test-output/corejet-requirements-"+story.getId()+".xml");
				OutputStream outputStream = new FileOutputStream(corejetReportOutputFile);
				requirementsCatalogue.write(outputStream,story);
				outputStream.flush();
			} catch (Exception e) {
				testProgressLogger.error("##### Failed to write story result",e);
			}
		}
	}

	/**
	 * @{inheritDoc
	 */
	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
	}

	private Throwable getThrowableCause(Throwable originalThrowable) {
		if (null!=originalThrowable.getCause()){
			return originalThrowable.getCause();
		} else {
			return originalThrowable;
		}
	}

	/**
	 * Prior to test method invocation log certain useful output.
	 * 
	 * @param method
	 */
	public void preTestMethodInvocation(CoreJetFrameworkMethod method) {

		Class<?> scenarioClass = method.getScenarioClass();
		classToFrameworkMethodMap.put(scenarioClass.getName(), method);

		testProgressLogger.info("Scenario: " + method.getScenario().getName());
	}



	/**
	 * All requirements should be marked pending until actually tested.
	 */
	private void setAllRequirementsAsToDo() {
		// Set status of all scenarios to pending - later we will only update
		// the status of scenarios we actually run
		for (Epic epic : requirementsCatalogue.getEpics()) {
			for (Story story : epic.getStories()) {
				for (Scenario scenario : story.getScenarios()) {

					// we ignore non-null statuses, as it is apparent that a
					// previous test execution has set this and we should ignore
					// it
					if (scenario.getStatus() == null) {
						scenario.setStatus(ScenarioStatus.TODO);
					}
				}
			}
		}
	}
}
