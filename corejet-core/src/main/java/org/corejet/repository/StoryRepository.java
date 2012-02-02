package org.corejet.repository;

import java.util.Map;

import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Story;
import org.corejet.repository.exception.StoryRepositoryException;

public interface StoryRepository {

	public Map<String, Story> getAllStories() throws StoryRepositoryException;

	public RequirementsCatalogue getRequirementsCatalogue() throws StoryRepositoryException;
}
