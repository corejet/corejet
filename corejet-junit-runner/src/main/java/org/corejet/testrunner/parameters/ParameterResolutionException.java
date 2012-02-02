package org.corejet.testrunner.parameters;

/**
 * An exception arising during resolution of step method parameters.
 * 
 * @author rnorth
 * 
 */
public class ParameterResolutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4290395863360585052L;

	public ParameterResolutionException(String string) {
		super(string);
	}

	public ParameterResolutionException(String string, Exception e) {
		super(string, e);
	}

}
