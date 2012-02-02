package org.corejet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An epic - a high level bundle of functionality
 */
public class Epic {

	private String id;
	private String title;
	private List<Story> stories;
	
	public Epic() {
		this.id = null;
		this.title = null;
		this.stories = new ArrayList<Story>();
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
	public List<Story> getStories() {
		return stories;
	}
	public void setStories(List<Story> stories) {
		this.stories = stories;
	}
	
	
	
}
