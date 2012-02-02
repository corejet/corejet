package org.corejet.testrunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit test runner which executes corejet-structured Story/Scenario classes in parallel.
 * 
 * @author rpickard
 *
 */
public class ParallelCoreJetTestRunner extends CoreJetTestRunner {

	private static int THREAD_POOL_SIZE = 5;
	private static final long CALL_TIMEOUT = 1800000l;
	private Class<?> testClass;

	private static final Logger logger = LoggerFactory.getLogger(ParallelCoreJetTestRunner.class);
	
	// Overwrite from system property if available
	static {
		String 	pool = System.getProperty("corejet.thread.pool");
		if (null!= pool){
			THREAD_POOL_SIZE = Integer.parseInt(pool);
		}
	}
	
	public ParallelCoreJetTestRunner(Class<?> testClass)
	throws CorejetException, InitializationError,
	SurplusScenarioException {
		super(testClass);
		this.testClass = testClass;
	}

	/* (non-Javadoc)
	 * @see org.junit.runners.ParentRunner#childrenInvoker(org.junit.runner.notification.RunNotifier)
	 */
	protected Statement childrenInvoker(final RunNotifier notifier) {
		return new Statement() {
			@Override
			public void evaluate() {
				runChildren(notifier);
			}
		};
	}

	private void runChildren(final RunNotifier notifier) {
		// run tests, Before and After tests will run automatically before and after each
		// test
		final List<FrameworkMethod> concurrentMethods = getChildren();
		final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

		final List<CoreJetCallableRunner> calls = new ArrayList<CoreJetCallableRunner>();

		for (final FrameworkMethod frameworkMethod : concurrentMethods) {
			try {
				calls.add(new CoreJetCallableRunner(frameworkMethod));
			} catch (Exception e) {
				logger.debug("Test construction stacktrace",e);
			}
		}
		

		try {
			executorService.invokeAll(calls, CALL_TIMEOUT, TimeUnit.MILLISECONDS);	
		} catch (InterruptedException e) {
			logger.debug("Test failure stacktrace",e);
		}
		
		executorService.shutdown();	
		
		
		// HACKY I KNOW, SUREFIRE USES A STUPID FLAG FOR FAILURES WHICH DOESN'T LET US REPORT WHEN RUNNING IN PARALLEL
		for (CoreJetCallableRunner call : calls){
			Description callDescription = describeChild(call.getMethod());
			notifier.fireTestStarted(callDescription);
			if(null!=call.getFailure()){
				notifier.fireTestFailure(new Failure(callDescription,call.getFailure()));
			}
			notifier.fireTestFinished(callDescription);
		}
		
	}


	private class CoreJetCallableRunner implements Callable<Object>  {

		private final FrameworkMethod method;
		
		private Throwable failure;

		CoreJetCallableRunner(FrameworkMethod method) {
			this.method = method;
		}

		public Object call() {
			try {
				methodBlock(method).evaluate();
			} catch (Throwable e) {
				failure = e;
			}
			return null;
		}
		
		public Throwable getFailure() {
			return failure;
		}
		
		public FrameworkMethod getMethod(){
			return method;
		}
	}
	
}
