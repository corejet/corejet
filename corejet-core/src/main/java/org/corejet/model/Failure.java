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

	public Failure(String failedStep, String cause, List<String> stackTrace) {
		this.failedStep = failedStep;
		this.cause = cause;
		this.stackTraceElements = stackTrace;
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

	private String getPrintableMessage(Throwable e) {
		return e.getClass().getCanonicalName()+" :: " + e.getMessage();
	}


	public String getFailedStep() {
		return failedStep;
	}

	public String getCause() {
		return cause;
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
