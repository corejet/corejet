/**
 * 
 */
package org.corejet.model.exception;

/**
 * @author rpickard
 *
 */
public class MergeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7966245203526388083L;

	/**
	 * 
	 */
	public MergeException() {

	}

	/**
	 * @param message
	 */
	public MergeException(String message) {
		super(message);

	}

	/**
	 * @param cause
	 */
	public MergeException(Throwable cause) {
		super(cause);

	}

	/**
	 * @param message
	 * @param cause
	 */
	public MergeException(String message, Throwable cause) {
		super(message, cause);

	}

}
