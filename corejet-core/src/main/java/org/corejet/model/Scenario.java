package org.corejet.model;

import java.util.LinkedHashMap;

import com.google.common.collect.Maps;

/**
 * Represents a behavioural scenario of a story
 */
public class Scenario {

	private String name;
	private LinkedHashMap<String,Double> givens;
	private LinkedHashMap<String,Double> whens;
	private LinkedHashMap<String,Double> thens;
	private ScenarioStatus status;
	private Failure failure;
	private String defect;
	private Story parentStory;

	public Scenario() {
		this.name = null;
		this.givens = Maps.newLinkedHashMap();
		this.whens = Maps.newLinkedHashMap();
		this.thens = Maps.newLinkedHashMap();
		this.status = null;
		this.parentStory = null;
	}

	public String getName() {
		return name;
	}
	
	public String getCleanedName() {
		return name.replaceAll("\\s+", "_");
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedHashMap<String,Double> getGivens() {
		return givens;
	}

	public void setGivens(LinkedHashMap<String,Double> givens) {
		this.givens = givens;
	}

	public LinkedHashMap<String,Double> getWhens() {
		return whens;
	}

	public void setWhens(LinkedHashMap<String,Double> whens) {
		this.whens = whens;
	}

	public LinkedHashMap<String,Double> getThens() {
		return thens;
	}

	public void setThens(LinkedHashMap<String,Double> thens) {
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

	private boolean isNullOrEmpty(LinkedHashMap<String,Double> map){
		if (null!=map){
			return map.size()==0;
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
		addGiven(text,0.0);
	}

	public void addWhen(String text) {
		addWhen(text,0.0);
	}

	public void addThen(String text) {
		addThen(text,0.0);
	}
	
	public void addGiven(String text, Double duration) {
		this.givens.put(text,duration);
	}

	public void addWhen(String text, Double duration) {
		this.whens.put(text,duration);
	}

	public void addThen(String text, Double duration) {
		this.thens.put(text,duration);
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
