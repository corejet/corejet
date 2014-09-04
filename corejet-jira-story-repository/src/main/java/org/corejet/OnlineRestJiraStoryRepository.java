package org.corejet;

import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.net.iharder.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Story;
import org.corejet.model.WritingException;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Story Repository which uses JIRA REST as its backend store.
 * 
 * @author rpickard
 *
 */
public class OnlineRestJiraStoryRepository implements StoryRepository {

	private Resty resty;
	private String restUrl;
	private String storyPointsFieldId;
	private String epicFieldId;
	private GherkinScenarioProcessor scenarioProcessor;
	private String acceptanceCriteriaFieldId;

	private static boolean initialized;
	private static Map<String,Story> stories = Maps.newHashMap();
	private static Map<String, Epic> epics = Maps.newHashMap();
	private static RequirementsCatalogue requirementsCatalogue;

	private static final int DEFAULT_POINTS = Integer.parseInt(System.getProperty("corejet.defaultpoints", "1"));
	private static final Logger logger = LoggerFactory.getLogger(OnlineRestJiraStoryRepository.class);

	public OnlineRestJiraStoryRepository() throws StoryRepositoryException {

		logger.info("Using REST api for Jira");
		
		scenarioProcessor = new GherkinScenarioProcessor();

		restUrl = Configuration.getProperty("jira.rest.url");

		resty = new Resty();

		String encoded = Base64.encodeBytes((Configuration.getProperty("jira.username") + ":" + Configuration.getProperty("jira.password")).getBytes());
		String value = "Basic " + new String(encoded);
		resty.alwaysSend("Authorization",value );

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
			JSONResource filterResponse = resty.json(restUrl+"/filter/"+Configuration.getProperty("jira.filter.id"));
			JSONResource searchResponse = resty.json(filterResponse.get("searchUrl").toString());
			JSONArray issues = (JSONArray)searchResponse.get("issues");
			for (int i = 0; i < issues.length() ; i++) {
				JSONObject issue = issues.getJSONObject(i);
				Story story = new Story();
				story.setId(getStringSafe(issue,"key"));
				issue = issue.getJSONObject("fields");
				story.setTitle(getStringSafe(issue,"summary"));

				String storyPointsAsString =getStringSafe(issue,"customfield_"+this.storyPointsFieldId);
				if (storyPointsAsString != null && !storyPointsAsString.equalsIgnoreCase("null") && !storyPointsAsString.trim().equals("")) {
					story.setPoints((int)Double.valueOf(storyPointsAsString.trim()).doubleValue());
				} else {
					story.setPoints(DEFAULT_POINTS);
					logger.warn("Story with ID:{} has not been estimated, setting to default value {}", story.getId(),DEFAULT_POINTS);
				}

				String epicAsString = "unknown";
				// If iterations is specified, use fix version, if neither is populated set to unknown
				if (!"iterations".equals(this.epicFieldId)){
					try {
						epicAsString = getStringSafe(issue.getJSONObject("customfield_"+this.epicFieldId),"value");
					} catch (JSONException e) {
						logger.warn("Failed to fing epic for story "+story.getId());
					}
				} else {
					try {
					JSONArray versions = issue.getJSONArray("fixVersions");
					// Use the first one or the first one with the word iteration in it
					epicAsString = versions.getJSONObject(0).getString("name");
					for(int j = 0; j < versions.length() ; j++){
						JSONObject version = versions.getJSONObject(j);
						if (version.getString("name").toLowerCase().contains("iteration")){
							epicAsString = version.getString("name");
							break;
						}
					}
					} catch (JSONException e){
						// no fix-version skip
					}
				}

				Epic epic = lookupOrCreateEpic(epicAsString);
				epic.getStories().add(story);
				story.setParentEpic(epic);

				String acceptanceCriteria = getStringSafe(issue,"customfield_"+this.acceptanceCriteriaFieldId);
				scenarioProcessor.parse(acceptanceCriteria , story);

				stories.put(story.getId(), story);

			}

		} catch (Exception e) {
			throw new StoryRepositoryException("Could not retrieve Story IDs", e);
		} 

		requirementsCatalogue.setExtractTime(new Date());
		requirementsCatalogue.setEpics(Lists.newArrayList(epics.values()));
		requirementsCatalogue.setProject(Configuration.getProperty("default.project"));

		initialized = true;
	}

	private String getStringSafe(JSONObject object, String key){
		try {
			return object.getString(key);
		} catch (JSONException e) {
			return null;
		}
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


	public RequirementsCatalogue getRequirementsCatalogue() throws StoryRepositoryException {

		if (!initialized) {
			initialize();
		}
		return requirementsCatalogue;
	}
}
