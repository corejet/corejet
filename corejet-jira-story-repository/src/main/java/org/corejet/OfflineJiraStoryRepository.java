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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Story repository which uses a cached <code>corejet-requirements.xml</code> file to provide
 * offline testing capability. 
 * <p>
 * The default location of the <code>corejet-requirements.xml</code> is 
 * set to be {@link Configuration#getBaseDirectory()} plus <code>/test-output/corejet-requirements.xml</code> 
 * i.e. it will never be cached.
 * </p>
 * <p>
 * The location of the <code>corejet-requirements.xml</code> can be specified by the system property
 * <code>corejet.cached.requirements.file</code>
 * </p>
 * @author rnorth
 */
public class OfflineJiraStoryRepository implements StoryRepository {

	private static final Logger logger = LoggerFactory.getLogger(OfflineJiraStoryRepository.class);
	private static final String DEFAULT_CACHED_REQUIREMENTS_FILE_LOCATION = Configuration.getBaseDirectory()+"/test-output/corejet-requirements.xml";
	private static final String CACHED_REQUIREMENTS_FILE_PROPERTY = "corejet.cached.requirements.file";
	
	private RequirementsCatalogue requirementsCatalogue;
	
	/**
	 * A cached <code>corejet-requirements.xml</code> file containing all stories
	 */
	public static File corejetRequirementsInputFile;
	
	// Allow users to specify an alternative cached requirements file
	static {
		String cachedRequirementsFileLocation = Configuration.getProperty(CACHED_REQUIREMENTS_FILE_PROPERTY);
		if (null==cachedRequirementsFileLocation || "".equals(cachedRequirementsFileLocation)) {
			// Fall back to the default location
			cachedRequirementsFileLocation = DEFAULT_CACHED_REQUIREMENTS_FILE_LOCATION;
		}
		logger.debug("Cached requirements file location = {}", cachedRequirementsFileLocation);
		corejetRequirementsInputFile = new File(cachedRequirementsFileLocation);
	}

	/**
	 * Instantiate the requirements catalogue from the file in the location specified by
	 * {@link OfflineJiraStoryRepository#corejetRequirementsInputFile}
	 * @throws FileNotFoundException
	 * @throws ParsingException
	 */
	public OfflineJiraStoryRepository() throws FileNotFoundException, ParsingException {
		requirementsCatalogue = new RequirementsCatalogue(new FileInputStream(corejetRequirementsInputFile));
	}

	/**
	 * @{inheritDoc}
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
