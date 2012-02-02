package org.corejet;


public class CoreJetStoryParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1815841688636387424L;

	public CoreJetStoryParsingException(String string) {
		super(string);
	}

	public CoreJetStoryParsingException(String string, Exception e) {
		super(string, e);
	}
}
