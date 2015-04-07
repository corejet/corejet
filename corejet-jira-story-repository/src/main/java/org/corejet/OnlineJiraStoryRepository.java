package org.corejet;

import hudson.plugins.jira.soap.JiraSoapService;
import hudson.plugins.jira.soap.JiraSoapServiceServiceLocator;
import hudson.plugins.jira.soap.RemoteAuthenticationException;
import hudson.plugins.jira.soap.RemoteCustomFieldValue;
import hudson.plugins.jira.soap.RemoteException;
import hudson.plugins.jira.soap.RemoteIssue;
import hudson.plugins.jira.soap.RemoteVersion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Story;
import org.corejet.model.WritingException;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Story Repository which uses JIRA as its backend store.
 * 
 * @author rnorth
 *
 */
public class OnlineJiraStoryRepository implements StoryRepository {

	private JiraSoapService service;
	private String securityToken;
	private String storyPointsFieldId;
	private String epicFieldId;
	private GherkinScenarioProcessor scenarioProcessor;
	private String acceptanceCriteriaFieldId;

	private static boolean initialized;
	private static Map<String,Story> stories = Maps.newHashMap();
	private static Map<String, Epic> epics = Maps.newHashMap();
	private static RequirementsCatalogue requirementsCatalogue;

	private static final Logger logger = LoggerFactory.getLogger(OnlineJiraStoryRepository.class);

	public OnlineJiraStoryRepository() throws StoryRepositoryException {

		scenarioProcessor = new GherkinScenarioProcessor();

		String wsdlUrlString = Configuration.getProperty("jira.soap.wsdl.url");

		try {
			URL wsdlUrl = new URL(wsdlUrlString);
			service = new JiraSoapServiceServiceLocator().getJirasoapserviceV2(wsdlUrl);
			securityToken = service.login(Configuration.getProperty("jira.username"), Configuration.getProperty("jira.password"));
		} catch (MalformedURLException e) {
			throw new StoryRepositoryException("Could not open connection to JIRA - malformed URL " + wsdlUrlString, e);
		} catch (ServiceException e) {
			throw new StoryRepositoryException("Could not open connection to JIRA", e);
		} catch (RemoteAuthenticationException e) {
			throw new StoryRepositoryException("Could not open connection to JIRA - authentication issue", e);
		} catch (RemoteException e) {
			throw new StoryRepositoryException("Could not open connection to JIRA", e);
		} catch (java.rmi.RemoteException e) {
			throw new StoryRepositoryException("Could not open connection to JIRA", e);
		}

		this.storyPointsFieldId = Configuration.getProperty("jira.storypoints.fieldid");
		this.epicFieldId = Configuration.getProperty("jira.epic.fieldid");
		this.acceptanceCriteriaFieldId = Configuration.getProperty("jira.acceptancecriteria.fieldid");
	}

	/**  
	 * Get all stories and create the {@link OfflineJiraStoryRepository#corejetRequirementsInputFile}
	 * 
	 */
	public Map<String, Story> getAllStories() throws StoryRepositoryException {

		if (!initialized) {
			initialize();
		}
		
		createCorejetRequirementsInputFile();
		return Collections.unmodifiableMap(stories);
	}

	/**
	 * Write the {@link RequirementsCatalogue} to file for use by the {@link OfflineJiraStoryRepository}
	 */
	private void createCorejetRequirementsInputFile() {
		try {
			File corejetReportInputFile = OfflineJiraStoryRepository.corejetRequirementsInputFile;
			logger.info("Re-creating cached requirements file at: {}", corejetReportInputFile.getAbsolutePath());
			File corejetReportRepositoryFile = corejetReportInputFile;
			OutputStream outputStream;
			outputStream = new FileOutputStream(corejetReportRepositoryFile);
			requirementsCatalogue.write(outputStream);
			outputStream.flush();
		} catch (FileNotFoundException e) {
			logger.error("Error writing new requirements file", e);
		} catch (WritingException e) {
			logger.error("Error writing new requirements file", e);
		} catch (IOException e) {
			logger.error("Error writing new requirements file", e);
		}
	}

	private synchronized void initialize() throws StoryRepositoryException {

		requirementsCatalogue = new RequirementsCatalogue();

		try {
			RemoteIssue[] issues = service.getIssuesFromFilter(securityToken, Configuration.getProperty("jira.filter.id"));
			for (RemoteIssue issue : issues) {
				Story story = new Story();
				story.setId(issue.getKey());
				story.setTitle(issue.getSummary());

				String storyPointsAsString = singleValueCustomFieldForIssue(issue, this.storyPointsFieldId);
				if (storyPointsAsString != null && !storyPointsAsString.trim().equals("")) {
					story.setPoints(Integer.valueOf(storyPointsAsString.trim()));
				} else {
					logger.debug("Story with ID:{} has not been estimated. Treating as one point", issue.getKey());
					story.setPoints(1);
				} 

				String epicAsString = "unknown";
				// If iterations is specified, use fix version, if neither is populated set to unknown
				if (!"iterations".equals(epicFieldId)){
					epicAsString = singleValueCustomFieldForIssue(issue, epicFieldId);
				} else {
					RemoteVersion[] versions = issue.getFixVersions();
					// Use the first one or the first one with the word iteration in it
					epicAsString = versions[0].getName();
					for(RemoteVersion version : versions){
						if (version.getName().toLowerCase().contains("iteration")){
							epicAsString = version.getName();
							break;
						}
					}
				}

				Epic epic = lookupOrCreateEpic(epicAsString);
				epic.getStories().add(story);
				story.setParentEpic(epic);

				String acceptanceCriteria = singleValueCustomFieldForIssue(issue, acceptanceCriteriaFieldId);
				scenarioProcessor.parse(acceptanceCriteria , story);

				stories.put(issue.getKey(), story);

			}

		} catch (RemoteException e) {
			throw new StoryRepositoryException("Could not retrieve Story IDs", e);
		} catch (java.rmi.RemoteException e) {
			throw new StoryRepositoryException("Could not retrieve Story IDs", e);
		}

		requirementsCatalogue.setExtractTime(new Date());
		requirementsCatalogue.setEpics(Lists.newArrayList(epics.values()));
		requirementsCatalogue.setProject(Configuration.getProperty("default.project"));

		initialized = true;
	}

	/**
	 * Get an 'Epic', creating a new one if necessary.
	 * 
	 * @param epicAsString
	 * @return
	 */
	private Epic lookupOrCreateEpic(String epicAsString) {
		if (epics.containsKey(epicAsString)) {
			return epics.get(epicAsString);
		}

		Epic newEpic = new Epic();
		newEpic.setTitle(epicAsString);
		newEpic.setId(epicAsString);

		epics.put(epicAsString, newEpic);
		return newEpic;
	}

	/**
	 * Get all values of a specific custom field for a given issue
	 * 
	 * @param issue
	 * @param fieldId
	 * @return
	 */
	private List<String> customFieldForIssue(RemoteIssue issue, String fieldId) {

		for (RemoteCustomFieldValue fieldValue : issue.getCustomFieldValues()) {
			String customfieldId = fieldValue.getCustomfieldId().replaceAll("[^\\d]", "");

			if (customfieldId.equals(fieldId)) {
				String[] fieldMultiValues = fieldValue.getValues();

				return Arrays.asList(fieldMultiValues);
			}
		}
		return null;
	}

	/**
	 * Get just the first value of a specific custom field for a given issue.
	 * 
	 * @param issue
	 * @param fieldId
	 * @return
	 * @throws StoryRepositoryException 
	 */
	private String singleValueCustomFieldForIssue(RemoteIssue issue, String fieldId) throws StoryRepositoryException {
		List<String> fieldMultiValues = customFieldForIssue(issue, fieldId);

		if (fieldMultiValues == null) {
			return "";
		}

		if (fieldMultiValues.size() != 1) {
			// No varargs support in Slf4j API?
			logger.debug("Unexpected number of custom field values found for issue:{} and fieldId:{} - expected 1, found " + fieldMultiValues.size() + ". The first value will be returned.", issue.getKey(), fieldId);
		}

		if (fieldMultiValues.size() == 0) {
			throw new StoryRepositoryException("Could not find a custom field value for issue:{} and fieldId:{}!");
		}

		return fieldMultiValues.get(0);
	}

	public RequirementsCatalogue getRequirementsCatalogue() throws StoryRepositoryException {

		if (!initialized) {
			initialize();
		}
		return requirementsCatalogue;
	}
}
