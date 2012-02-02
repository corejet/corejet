package org.corejet.testrunner;

/**
 * An exception indicating that a class annotated with @Scenario has been found
 * which seems to be extraneous.
 * 
 * @author rnorth
 * 
 */
public class SurplusScenarioException extends Error {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9177214923124236026L;

	public SurplusScenarioException(String string) {
		super(string);
	}

}
