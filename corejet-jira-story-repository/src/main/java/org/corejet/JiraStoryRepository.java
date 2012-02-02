package org.corejet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Story;
import org.corejet.model.exception.ParsingException;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Story Repository which uses JIRA as its backend store. Can work in
 * online/offline modes by delegating to {@link OnlineJiraStoryRepository} or
 * {@link OfflineJiraStoryRepository} as specified by the user.
 * 
 * @author rnorth
 * 
 */
public class JiraStoryRepository implements StoryRepository {

	private static final Logger logger = LoggerFactory.getLogger(JiraStoryRepository.class);
	private StoryRepository delegate;

	/**
	 * Instantiation, using the value of system property
	 * corejet.repository.offline to determine whether working in offline mode
	 * or not.
	 * 
	 * @throws StoryRepositoryException
	 * @throws FileNotFoundException
	 * @throws ParsingException
	 */
	public JiraStoryRepository() throws StoryRepositoryException, FileNotFoundException, ParsingException {

		String offlineMode = System.getProperty("corejet.repository.offline");
		String neverCache = System.getProperty("corejet.repository.nevercache");
		File cachedFile = OfflineJiraStoryRepository.corejetReportInputFile;
		boolean cachedFileStale = !cachedFile.exists() || cachedFile.lastModified() < (System.currentTimeMillis() - 60 * 60 * 1000);
		
		
		if (Boolean.parseBoolean(offlineMode) && cachedFile.exists()) {
			logger.info("JiraStoryRepository in OFFLINE mode (-Dcorejet.repository.offline=true and recent local cached file at "+cachedFile.getAbsolutePath()+")");
			delegate = new OfflineJiraStoryRepository();
		} else {
			logger.debug("System property set or absent -Dcorejet.repository.offline=false");
			
			if ( Boolean.parseBoolean(neverCache) || cachedFileStale) {
				logger.debug("Cached file is stale or -Dcorejet.repository.nevercache=true - going ONLINE");
				delegate = new OnlineJiraStoryRepository();
			} else {
				logger.debug("Cached file is not stale - staying OFFLINE");
				delegate = new OfflineJiraStoryRepository();
			}
		}
	}

	/**  
	 * @{inheritDoc}
	 */
	public Map<String, Story> getAllStories() throws StoryRepositoryException {
		return delegate.getAllStories();
	}

	/**  
	 * @{inheritDoc}
	 */
	public RequirementsCatalogue getRequirementsCatalogue() throws StoryRepositoryException {
		return delegate.getRequirementsCatalogue();
	}
}
