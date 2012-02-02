/**
 * 
 */
package org.corejet.testrunner;

/**
 * @author rpickard
 *
 */
public class FailedMethodError extends AssertionError {

	/**
	 * 
	 */
	public FailedMethodError() {
		
	}

	/**
	 * @param message
	 */
	public FailedMethodError(String message) {
		super(message);
		
	}

	/**
	 * @param cause
	 */
	public FailedMethodError(Throwable cause) {
		super(cause);
		
	}
}
