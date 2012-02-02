package org.corejet.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents a behavioural scenario of a story
 */
public class Scenario {

	private String name;
	private List<String> givens;
	private List<String> whens;
	private List<String> thens;
	private ScenarioStatus status;
	private Failure failure;
	private String defect;
	private Story parentStory;

	public Scenario() {
		this.name = null;
		this.givens = Lists.newArrayList();
		this.whens = Lists.newArrayList();
		this.thens = Lists.newArrayList();
		this.status = null;
		this.parentStory = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getGivens() {
		return givens;
	}

	public void setGivens(List<String> givens) {
		this.givens = givens;
	}

	public List<String> getWhens() {
		return whens;
	}

	public void setWhens(List<String> whens) {
		this.whens = whens;
	}

	public List<String> getThens() {
		return thens;
	}

	public void setThens(List<String> thens) {
		this.thens = thens;
	}

	/**
	 * @return the status of the test or EMPTY if no steps)
	 */
	public ScenarioStatus getStatus() {
		if (isNullOrEmpty(givens)&&isNullOrEmpty(whens)&&isNullOrEmpty(thens)){
			return ScenarioStatus.EMPTY;
		} else {
			return status;
		}
	}

	private boolean isNullOrEmpty(List<String> list){
		if (null!=list){
			return list.size()==0;
		} else {
			return true;
		}
	}

	public void setStatus(ScenarioStatus status) {
		this.status = status;
	}

	public Story getParentStory() {
		return parentStory;
	}

	public void setParentStory(Story parentStory) {
		this.parentStory = parentStory;
	}

	public void addGiven(String text) {
		this.givens.add(text);
	}

	public void addWhen(String text) {
		this.whens.add(text);
	}

	public void addThen(String text) {
		this.thens.add(text);
	}

	public Failure getFailure() {
		return failure;
	}

	public void setFailure(Failure failure) {
		this.failure = failure;
	}

	public String getDefect() {
		return defect;
	}

	public void setDefect(String defect) {
		this.defect = defect;
	}


}
