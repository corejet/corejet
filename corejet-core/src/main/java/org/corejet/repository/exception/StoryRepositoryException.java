package org.corejet.repository.exception;

public class StoryRepositoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5414713241044316580L;

	public StoryRepositoryException(String string, Exception e) {
		super(string, e);
	}

	public StoryRepositoryException(String string) {
		super(string);
	}

}
