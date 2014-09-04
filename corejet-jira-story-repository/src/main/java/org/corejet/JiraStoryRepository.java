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
	 * <p>Instantiation. Use the value of system property <code>corejet.repository.offline</code> 
	 * to determine whether to work in offline mode or not.</p>
	 * 
	 * <p>In summary, if a cached requirements file is available, the default behaviour is 
	 * to go online only if the cached requirements file is more than one hour old.</p>
	 * 
	 * <p>If <code>corejet.repository.offline</code> is 'false' (or not set) and the value of
	 * system property <code>corejet.repository.nevercache</code> is 'true', always go online.</p>
	 * 
	 * <p>If <code>corejet.repository.offline</code> is 'false' (or not set) and <code>corejet.repository.nevercache</code> is 'false',
	 * only go online if the cached repository file is not present or if it is more than one hour old.</p>
	 * 
	 * <p>If <code>corejet.repository.offline</code> is 'true' and a cached requirements file exists,
	 * always work offline, regardless of when the cached requirements file was last updated</p>
	 * 
	 * @throws StoryRepositoryException
	 * @throws FileNotFoundException
	 * @throws ParsingException
	 */
	public JiraStoryRepository() throws StoryRepositoryException, FileNotFoundException, ParsingException {

		String offlineMode = System.getProperty("corejet.repository.offline");
		String neverCache = System.getProperty("corejet.repository.nevercache");
		File cachedRepositoryFile = OfflineJiraStoryRepository.corejetRequirementsInputFile;
		boolean cachedFileStale = !cachedRepositoryFile.exists() || cachedRepositoryFile.lastModified() < (System.currentTimeMillis() - 60 * 60 * 1000);
		if (Boolean.parseBoolean(offlineMode) && cachedRepositoryFile.exists()) {
			logger.info("JiraStoryRepository in OFFLINE mode (-Dcorejet.repository.offline=true) and recent local cached file exists at "+cachedRepositoryFile.getAbsolutePath()+")");
			delegate = new OfflineJiraStoryRepository();
		} else {
			logger.debug("System property set or absent -Dcorejet.repository.offline=false");
			if ( Boolean.parseBoolean(neverCache) || cachedFileStale) {
				logger.info("Cached file is stale or -Dcorejet.repository.nevercache=true - going ONLINE");
				if (null!=Configuration.getProperty("jira.rest.url")){
					delegate = new OnlineRestJiraStoryRepository();
				} else {
					delegate = new OnlineJiraStoryRepository();
				}
			} else {
				logger.info("Cached file is not stale - staying OFFLINE");
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
