package org.corejet.testrunner;

public class CorejetException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 3149539549607578186L;

	public CorejetException(String string, Exception e) {
		super(string, e);
	}
}
