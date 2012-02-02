package org.corejet;

import java.util.List;

import org.corejet.model.Story;

import gherkin.formatter.Formatter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

public class CoreJetGherkinFormatter implements Formatter {

	private final Story story;
	private org.corejet.model.Scenario currentScenario;
	private List<String> lastModifiedStepList;

	public CoreJetGherkinFormatter(Story story) {
		this.story = story;
	}

	public void uri(String uri) {
		
	}

	public void feature(Feature feature) {
		
	}

	public void background(Background background) {
		throw new UnsupportedOperationException("Background not supported yet");
	}

	public void scenario(Scenario scenario) {
		currentScenario = new org.corejet.model.Scenario();
		story.getScenarios().add(currentScenario);
		currentScenario.setParentStory(story);
		
		currentScenario.setName(scenario.getName());
	}

	public void scenarioOutline(ScenarioOutline scenarioOutline) {
		throw new UnsupportedOperationException("Scenario Outline not supported yet");
	}

	public void examples(Examples examples) {
		throw new UnsupportedOperationException("Examples not supported yet");
	}

	public void step(Step step) {
		String keyword = step.getKeyword().trim().toLowerCase();
		
		if ("given".equals(keyword)) {
			currentScenario.getGivens().add(step.getName());
			lastModifiedStepList = currentScenario.getGivens();
		}
		
		if ("when".equals(keyword)) {
			currentScenario.getWhens().add(step.getName());
			lastModifiedStepList = currentScenario.getWhens();
		}
		
		if ("then".equals(keyword)) {
			currentScenario.getThens().add(step.getName());
			lastModifiedStepList = currentScenario.getThens();
		}
		
		if ("and".equals(keyword)) {
			lastModifiedStepList.add(step.getName());
		}
	}

	public void eof() {
		
	}

	public void syntaxError(String state, String event, List<String> legalEvents, String uri, int line) {
		throw new CoreJetStoryParsingException(String.format("State: %s, Event: %s, LegalEvents: %s, Line: %d", state, event, legalEvents.toString(), line));
	}

}
