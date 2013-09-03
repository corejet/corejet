package org.corejet.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.corejet.AlphanumComparator;
import org.corejet.model.exception.MergeException;
import org.corejet.model.exception.ParsingException;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a requirements catalogue containing
 * epics, stories and scenarios.
 */
public class RequirementsCatalogue implements Cloneable{
	
	private static final Logger logger = LoggerFactory.getLogger(RequirementsCatalogue.class);

	private static final String DURATION = "duration";
	private Date extractTime;
	private Date testTime;
	private String project;
	private List<Epic> epics;

	private static DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public RequirementsCatalogue() {
		this.extractTime = null;
		this.testTime = null;
		this.project = null;
		this.epics = new ArrayList<Epic>();
	}

	public RequirementsCatalogue(InputStream input) throws ParsingException {
		this();
		populate(input);
	}

	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public List<Epic> getEpics() {
		return epics;
	}
	public void setEpics(List<Epic> epics) {
		this.epics = epics;
	}
	public Date getExtractTime() {
		return extractTime;
	}
	public void setExtractTime(Date extractTime) {
		this.extractTime = extractTime;
	}
	public Date getTestTime() {
		return testTime;
	}
	public void setTestTime(Date testTime) {
		this.testTime = testTime;
	}

	/**
	 * Populate this catalogue from an input stream
	 * @param input Stream containing XML data
	 * @throws ParsingException If the file format is invalid
	 */
	@SuppressWarnings("unchecked")
	public void populate(InputStream input) throws ParsingException {

		// 1. Parse the document into a JDom tree
		SAXBuilder parser = new SAXBuilder();
		Document doc;

		try {
			doc = parser.build(input);
		} catch(IOException e) {
			throw new ParsingException(e);
		} catch(JDOMException e) {
			throw new ParsingException(e);
		}

		// 2. Read the root element and populate the RequirementsCatalogue object
		Element catalogueElement = doc.getRootElement();
		this.setProject(catalogueElement.getAttributeValue("project"));

		try {
			this.setExtractTime(isoDateTimeFormat.parse(catalogueElement.getAttributeValue("extractTime")));
		} catch(ParseException e) {
			throw new ParsingException(e);
		}

		List<Epic> epics = new ArrayList<Epic>();
		this.setEpics(epics);

		// 3. Read each epic
		for(Element epicElement : (List<Element>) catalogueElement.getChildren("epic")) {

			Epic epic = new Epic();
			epic.setId(epicElement.getAttributeValue("id"));
			epic.setTitle(epicElement.getAttributeValue("title"));
			epics.add(epic);

			// 4. Read each story
			List<Story> stories = new ArrayList<Story>();
			epic.setStories(stories);

			for(Element storyElement : (List<Element>) epicElement.getChildren("story")) {

				Story story = new Story();
				String storyId = storyElement.getAttributeValue("id");
				story.setId(storyId);
				story.setTitle(storyElement.getAttributeValue("title"));
				String points = storyElement.getAttributeValue("points");
				if (null!=points && !"".equals(points.trim())) {
					story.setPoints(Integer.parseInt(points));
				} else {
					logger.debug("Story with ID:{} has not been estimated.", storyId);
				}
				story.setRequirementStatus(storyElement.getAttributeValue("requirementStatus"));
				story.setRequirementResolution(storyElement.getAttributeValue("requirementResolution"));
				story.setPriority(storyElement.getAttributeValue("priority"));
				story.setParentEpic(epic);
				stories.add(story);

				List<Scenario> scenarios = new ArrayList<Scenario>();
				story.setScenarios(scenarios);

				for(Element scenarioElement : (List<Element>) storyElement.getChildren("scenario")) {

					Scenario scenario = new Scenario();
					scenario.setName(scenarioElement.getAttributeValue("name"));

					scenario.setDefect(scenarioElement.getAttributeValue("defect"));

					String scenarioStatus = scenarioElement.getAttributeValue("testStatus");
					if(scenarioStatus != null) {
						scenario.setStatus(ScenarioStatus.valueOf(scenarioStatus.toUpperCase()));
					}

					for (Element givenElement : (List<Element>) scenarioElement.getChildren("given")) {
						if (null!=givenElement.getAttribute(DURATION)){
							try {
								scenario.addGiven(givenElement.getText(),givenElement.getAttribute(DURATION).getDoubleValue());
							} catch (DataConversionException e){
								scenario.addGiven(givenElement.getText());
							}
						} else {
							scenario.addGiven(givenElement.getText());
						}
					}
					for (Element whenElement : (List<Element>) scenarioElement.getChildren("when")) {
						if (null!=whenElement.getAttribute(DURATION)){
							try {
								scenario.addWhen(whenElement.getText(),whenElement.getAttribute(DURATION).getDoubleValue());
							} catch (DataConversionException e){
								scenario.addWhen(whenElement.getText());
							}
						} else {
							scenario.addWhen(whenElement.getText());
						}
					}
					for (Element thenElement : (List<Element>) scenarioElement.getChildren("then")) {
						if (null!=thenElement.getAttribute(DURATION)){
							try {
								scenario.addThen(thenElement.getText(),thenElement.getAttribute(DURATION).getDoubleValue());
							} catch (DataConversionException e){
								scenario.addThen(thenElement.getText());
							}
						} else {
							scenario.addThen(thenElement.getText());
						}
					}

					Element failureElement = scenarioElement.getChild("failure");

					if (null!=failureElement){
						List<String> lines = new ArrayList<String>();
						for (Object line : failureElement.getChildren("line")){
							lines.add(((Element)line).getText());
						}
						scenario.setFailure(new Failure(failureElement.getAttributeValue("step"), failureElement.getAttributeValue("cause"), lines,failureElement.getAttributeValue("link")));						
					}

					scenario.setParentStory(story);
					scenarios.add(scenario);
				}
			}
		}
	}


	/**
	 * Write XML representation to an output stream
	 * @param output Stream to write to
	 * @throws WritingException If the output stream cannot be written to
	 */
	public void write(OutputStream output) throws WritingException {
		write(output, null);
	}

	/**
	 * Write XML representation to an output stream
	 * @param output Stream to write to
	 * @param storyToWrite An individual story to write, if null, writes all
	 * @throws WritingException If the output stream cannot be written to
	 */
	public void write(OutputStream output, Story storyToWrite) throws WritingException {

		// 1. Create root element and add to new document

		Element requirementsCatalogueElement = new Element("requirementscatalogue");

		requirementsCatalogueElement.setAttribute("project", this.getProject());

		if(this.getExtractTime() != null) {
			requirementsCatalogueElement.setAttribute("extractTime", isoDateTimeFormat.format(this.getExtractTime()));
		}

		if(this.getTestTime() != null) {
			requirementsCatalogueElement.setAttribute("testTime", isoDateTimeFormat.format(this.getTestTime()));
		}

		Document doc = new Document(requirementsCatalogueElement);

		// 2. Create epics

		if(this.getEpics() != null) {

			for(Epic epic : sortEpics(this.getEpics())) {

				if (null!=epic.getStories() && (epic.getStories().contains(storyToWrite) || null == storyToWrite)){

					Element epicElement = new Element("epic");
					epicElement.setAttribute("id", epic.getId());
					epicElement.setAttribute("title", epic.getTitle());
					requirementsCatalogueElement.addContent(epicElement);

					// 3. Create stories
					for(Story story : sortStories(epic.getStories())) {
						if (story.equals(storyToWrite) || null == storyToWrite){
							Element storyElement = new Element("story");
							storyElement.setAttribute("id", story.getId());
							storyElement.setAttribute("title", story.getTitle());

							if(story.getPoints() != null) {
								storyElement.setAttribute("points", story.getPoints().toString());
							}

							if(story.getRequirementStatus() != null) {
								storyElement.setAttribute("requirementStatus", story.getRequirementStatus());
							}

							if(story.getRequirementResolution() != null) {
								storyElement.setAttribute("requirementResolution", story.getRequirementResolution());
							}

							if(story.getPriority() != null) {
								storyElement.setAttribute("priority", story.getPriority());
							}

							epicElement.addContent(storyElement);

							// 4. Create scenarios
							if(story.getScenarios() != null) {
								for(Scenario scenario : sortScenarios(story.getScenarios())) {
									Element scenarioElement = new Element("scenario");
									scenarioElement.setAttribute("name", scenario.getName());
									Double runningTime = 0.0;

									if (null!=scenario.getDefect()){
										scenarioElement.setAttribute("defect", scenario.getDefect());
									}

									if(scenario.getStatus() != null) {
										scenarioElement.setAttribute("testStatus", scenario.getStatus().getName());
									}

									for (Entry<String, Double> given : scenario.getGivens().entrySet()) {
										Element element = new Element("given");
										element.addContent(given.getKey());
										if (null!=given.getValue()){
											runningTime = runningTime + round(given.getValue());
											element.setAttribute(DURATION, round(given.getValue()).toString());
										}
										scenarioElement.addContent(element);
									}

									for (Entry<String, Double> when : scenario.getWhens().entrySet()) {
										Element element = new Element("when");
										element.addContent(when.getKey());
										if (null!=when.getValue()){
											runningTime = runningTime + round(when.getValue());
											element.setAttribute(DURATION, round(when.getValue()).toString());
										}
										scenarioElement.addContent(element);
									}

									for (Entry<String, Double> then : scenario.getThens().entrySet()) {
										Element element = new Element("then");
										element.addContent(then.getKey());
										if (null!=then.getValue()){
											runningTime = runningTime + round(then.getValue());
											element.setAttribute(DURATION, round(then.getValue()).toString());
										}
										scenarioElement.addContent(element);
									}

									scenarioElement.setAttribute(DURATION,round(runningTime).toString());
									if (null!=scenario.getFailure()){
										Failure failure = scenario.getFailure();
										Element failureElement = new Element("failure");
										failureElement.setAttribute("step",failure.getFailedStep());
										failureElement.setAttribute("cause",failure.getCause());
										if(null!=failure.getLink()){
											failureElement.setAttribute("link",failure.getLink());
										}
										for (String line : failure.getStackTrace()){
											failureElement.addContent(new Element("line").addContent(line));
										}
										scenarioElement.addContent(failureElement);
									}

									storyElement.addContent(scenarioElement);

								}
							}
						}
					}
				}
			}
		}

		// 5. Write to the output stream
		try {
			new XMLOutputter().output(doc, output);
		} catch (IOException e) {
			throw new WritingException(e);
		}
	}

	/**
	 * Merges two Requirement Catalogues
	 * @param catA
	 * @param catB
	 * @return - A new {@link RequirementsCatalogue} representing the merger of the two
	 * @throws MergeException 
	 */
	public static RequirementsCatalogue merge(RequirementsCatalogue catA, RequirementsCatalogue catB) throws MergeException{

		if (null==catA){
			return catB;
		} else if(null==catB){
			return catA;
		}

		if (!catA.getProject().equals(catB.getProject())){
			throw new MergeException("Provided Catalogues where not for the same project");
		}

		RequirementsCatalogue master;
		RequirementsCatalogue additions;

		// Use the latest version when in doubt
		if (catA.getExtractTime().after(catB.getExtractTime())){
			master = catA;
			additions = catB;
		} else {
			master = catB; 
			additions = catA;
		}

		RequirementsCatalogue merged = master.clone();


		// Copy in additions
		for (Epic additionEpic : additions.getEpics()){
			Epic mergedEpic = null;
			for (Epic epic : merged.getEpics()){
				if (epic.getId().equals(additionEpic.getId())){
					mergedEpic = epic;
					break;
				}
			}
			if (null!=mergedEpic){
				for (Story additionStory : additionEpic.getStories()){
					Story mergedStory = null;
					for (Story story : mergedEpic.getStories()){
						if (story.getId().equals(additionStory.getId())){
							mergedStory = story;
							break;
						}
					}
					if (null!=mergedStory){
						for (Scenario additionScenario : additionStory.getScenarios()){
							Scenario mergedScenario = null;
							for (Scenario scenario : mergedStory.getScenarios()){
								if (scenario.getName().equals(additionScenario.getName())){
									mergedScenario = scenario;
									break;
								}
							}
							if (null!=mergedScenario){

								// Master scenario is always the most up to date so use it's value unless it is ToDo
								if(mergedScenario.getStatus().equals(ScenarioStatus.TODO) || null == mergedScenario.getStatus() ){
									if (null==additionScenario.getStatus()){
										mergedScenario.setStatus(ScenarioStatus.TODO);
									} else {
										// decide which scenario timings to use based on total time
										// i.e. if the total time of the addition > 0 use it
										Double additionTime = 0.0;
										for (Double time : additionScenario.getGivens().values()){
											additionTime +=time;
										}
										for (Double time : additionScenario.getWhens().values()){
											additionTime +=time;
										}
										for (Double time : additionScenario.getThens().values()){
											additionTime +=time;
										}

										if (additionTime>0){
											mergedScenario.setGivens(additionScenario.getGivens());
											mergedScenario.setWhens(additionScenario.getWhens());
											mergedScenario.setThens(additionScenario.getThens());
										}
										mergedScenario.setStatus(additionScenario.getStatus());
										mergedScenario.setDefect(additionScenario.getDefect());
										mergedScenario.setFailure(additionScenario.getFailure());
									}
								}
							} else {
								mergedStory.getScenarios().add(additionScenario);
							}
						}
					} else {
						mergedEpic.getStories().add(additionStory);
					}
				}
			} else {
				merged.getEpics().add(additionEpic);
			}
		}

		return merged;

	}

	@SuppressWarnings("unchecked")
	private List<Epic> sortEpics(List<Epic> unsorted){
		Map<String, Epic> epics = new HashMap<String, Epic>();
		List<String> epicIds = new ArrayList<String>();
		for (Epic epic : unsorted){
			epics.put(epic.getId(), epic);
			epicIds.add(epic.getId());
		}
		Collections.sort(epicIds,new AlphanumComparator());
		List<Epic> sorted = new ArrayList<Epic>();
		for (String epicId : epicIds){
			sorted.add(epics.get(epicId));
		}
		return sorted;
	}

	@SuppressWarnings("unchecked")
	private List<Story> sortStories(List<Story> unsorted){
		Map<String, Story> stories = new HashMap<String, Story>();
		List<String> storyIds = new ArrayList<String>();
		for (Story story : unsorted){
			stories.put(story.getId(), story);
			storyIds.add(story.getId());
		}
		Collections.sort(storyIds,new AlphanumComparator());
		List<Story> sorted = new ArrayList<Story>();
		for (String storyId : storyIds){
			sorted.add(stories.get(storyId));
		}
		return sorted;
	}

	@SuppressWarnings("unchecked")
	private List<Scenario> sortScenarios(List<Scenario> unsorted){
		Map<String, Scenario> scenarios = new HashMap<String, Scenario>();
		List<String> scenarioIds = new ArrayList<String>();
		for (Scenario scenario : unsorted){
			scenarios.put(scenario.getName(), scenario);
			scenarioIds.add(scenario.getName());
		}
		Collections.sort(scenarioIds,new AlphanumComparator());
		List<Scenario> sorted = new ArrayList<Scenario>();
		for (String scenarioId : scenarioIds){
			sorted.add(scenarios.get(scenarioId));
		}
		return sorted;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public RequirementsCatalogue clone(){
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			write(os);
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			return new RequirementsCatalogue(is);
		} catch (Exception e) {
			throw new RuntimeException("Failed to clone RequirementsCatalogue", e);
		}
	}

	private Double round(Double d){
		return Double.parseDouble(Long.toString(Math.round(d*100))) / 100;
	}


}

