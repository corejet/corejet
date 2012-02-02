package org.corejet.model;

/**
 * Represents the status of a given scenario that has been executed
 */
public enum ScenarioStatus {

	PASS("pass"),
	FAIL("fail"),
	TODO("todo"),
	NA("na"),
	EMPTY("empty"),
	PENDING("pending"),
	MISMATCH("mismatch"),
	SUPERFLUOUS("superfluous"),
	DEFECT("defect");
	
	String name;
	
	private ScenarioStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
