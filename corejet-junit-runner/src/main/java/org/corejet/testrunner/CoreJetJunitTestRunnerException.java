package org.corejet.testrunner;

/**
 * An exception arising during the execution of the {@link CoreJetTestRunner}.
 * 
 * @author rnorth
 * 
 */
public class CoreJetJunitTestRunnerException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = 314288631251549525L;

	public CoreJetJunitTestRunnerException(String string) {
		super(string);
	}

	public CoreJetJunitTestRunnerException(String string, Exception e) {
		super(string, e);
	}
}
