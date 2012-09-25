package org.corejet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a failure of a Scenario
 * @author rpickard
 *
 */
public class Failure {

	private final List<String> stackTraceElements;
	private final String cause;
	private final String failedStep;
	// optional link to provide additional information
	private String link;

	public Failure(String failedStep, String cause, List<String> stackTrace) {
		this.failedStep = failedStep;
		this.cause = cause;
		this.stackTraceElements = stackTrace;
	}
	
	public Failure(String failedStep, String cause, List<String> stackTrace,String link) {
		this.failedStep = failedStep;
		this.cause = cause;
		this.stackTraceElements = stackTrace;
		this.link = link;
	}

	public Failure(String failedStep, Throwable e){
		this.failedStep = failedStep;
		if (null!=e.getCause()){
			Throwable cause = e.getCause();
			this.cause = getPrintableMessage(cause);
			this.stackTraceElements = stackTraceToStringArray(e.getCause(), 20);
		} else {
			this.cause = getPrintableMessage(e);
			this.stackTraceElements = stackTraceToStringArray(e, 20);
		}

	}
	public Failure(String failedStep, Throwable e, String link){
		this.link = link;
		this.failedStep = failedStep;
		if (null!=e.getCause()){
			Throwable cause = e.getCause();
			this.cause = getPrintableMessage(cause);
			this.stackTraceElements = stackTraceToStringArray(e.getCause(), 20);
		} else {
			this.cause = getPrintableMessage(e);
			this.stackTraceElements = stackTraceToStringArray(e, 20);
		}
	}

	protected String getPrintableMessage(Throwable e) {
		return e.getClass().getCanonicalName()+" :: " + e.getMessage();
	}


	public String getFailedStep() {
		return failedStep;
	}

	public String getCause() {
		return cause;
	}
	
	public String getLink() {
		return link;
	}

	public List<String> getStackTrace() {
		return stackTraceElements;
	}

	private List<String> stackTraceToStringArray(Throwable e, int maxLines) {
		List<String> elements = new ArrayList<String>();
		int i = 0;
		for (StackTraceElement element : e.getStackTrace()) {
			if (i<maxLines){
				elements.add(element.toString());
			}
			i++;
		}
		if (e.getStackTrace().length>maxLines){
			elements.add("... "+ (e.getStackTrace().length-maxLines) +" more");
		}
		return elements;
	}
}
