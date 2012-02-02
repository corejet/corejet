package org.corejet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a story - a logical deliverable of functionality.
 */
public class Story {

	private String id;
	private String title;
	private Integer points;
	private String requirementStatus;
	private String requirementResolution;
	private String priority;
	private List<Scenario> scenarios;
	private Epic parentEpic;
	
	public Story() {
		this.id = null;
		this.title = null;
		this.points = null;
		this.requirementStatus = null;
		this.requirementResolution = null;
		this.priority = null;
		this.scenarios = new ArrayList<Scenario>();
		this.parentEpic = null;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
	public String getRequirementStatus() {
		return requirementStatus;
	}
	public void setRequirementStatus(String requirementStatus) {
		this.requirementStatus = requirementStatus;
	}
	public String getRequirementResolution() {
		return requirementResolution;
	}
	public void setRequirementResolution(String requirementResolution) {
		this.requirementResolution = requirementResolution;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public List<Scenario> getScenarios() {
		return scenarios;
	}
	public void setScenarios(List<Scenario> scenarios) {
		this.scenarios = scenarios;
	}
	public Epic getParentEpic() {
		return parentEpic;
	}
	public void setParentEpic(Epic parentEpic) {
		this.parentEpic = parentEpic;
	}

	public boolean hasScenarioNamed(String scenarioName) {
		for (Scenario s : scenarios) {
			if (s.getName().equals(scenarioName)) {
				return true;
			}
		}
		return false;
	}
	
}
