package org.corejet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Story;
import org.corejet.model.exception.ParsingException;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;

import com.google.common.collect.Maps;

/**
 * Story repository which uses a cached corejet-requirements.xml file to provide
 * offline testing capability.
 * 
 * @author rnorth
 * 
 */
public class OfflineJiraStoryRepository implements StoryRepository {

	private RequirementsCatalogue requirementsCatalogue;
	public static File corejetReportInputFile = new File(Configuration.getBaseDirectory()+"/test-output/corejet-requirements.xml");

	public OfflineJiraStoryRepository() throws FileNotFoundException, ParsingException {
		requirementsCatalogue = new RequirementsCatalogue(new FileInputStream(corejetReportInputFile));
	}

	/**
	 * @{inheritDoc
	 */
	public Map<String, Story> getAllStories() throws StoryRepositoryException {

		Map<String, Story> result = Maps.newHashMap();
		for (Epic epic : requirementsCatalogue.getEpics()) {
			for (Story story : epic.getStories()) {
				result.put(story.getId(), story);
			}
		}

		return result;
	}

	/**
	 * @{inheritDoc
	 */
	public RequirementsCatalogue getRequirementsCatalogue() throws StoryRepositoryException {
		return requirementsCatalogue;
	}

}
