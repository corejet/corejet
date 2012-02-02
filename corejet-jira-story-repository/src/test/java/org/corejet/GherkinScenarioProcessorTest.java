package org.corejet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.corejet.model.Scenario;
import org.corejet.model.Story;
import org.junit.Before;
import org.junit.Test;

public class GherkinScenarioProcessorTest {

	private static final String SIMPLE_SCENARIO = "Scenario: Simple Scenario \n" +
					"  Given I have a simple scenario text \n" +
					"  When I parse it using Gherkin \n" +
					"  Then I get a scenario in Corejet object format \n";

	private static final String SIMPLE_SCENARIO_WITH_COMMENT = "Scenario: Simple Scenario \n" +
			"  Given I have a simple scenario text \n" +
			"  When I parse it using Gherkin \n" +
			"// Comment goes here \n" +
			"  Then I get a scenario in Corejet object format \n";
	
	private static final String SCENARIO_WITH_AND = "Scenario: Simple Scenario \n" +
	"  Given I have a simple scenario text \n" +
	"  And an And condition is set \n" +
	"  When I parse it using Gherkin \n" +
	"  Then I get a scenario in Corejet object format \n" +
	"  And there is more than one Given \n" +
	"  And there is more than one Then";

	private GherkinScenarioProcessor scenarioProcessor;
	private Story storyToPopulate;

	@Before
	public void setUp() {
		scenarioProcessor = new GherkinScenarioProcessor();
		
		storyToPopulate = new Story();
		storyToPopulate.setTitle("title");
	}
	
	@Test
	public void parseSimple() {
		
		scenarioProcessor.parse(
				SIMPLE_SCENARIO, storyToPopulate );
		
		List<Scenario> scenarios = storyToPopulate.getScenarios();
		
		assertTrue(scenarios.size() == 1);
		Scenario scenario = scenarios.get(0);
		assertEquals("Simple Scenario", scenario.getName());

		assertTrue(scenario.getGivens().size() == 1);
		assertEquals("I have a simple scenario text", scenario.getGivens().get(0));
		assertEquals("I parse it using Gherkin", scenario.getWhens().get(0));
		assertEquals("I get a scenario in Corejet object format", scenario.getThens().get(0));
	}
	
	@Test
	public void parseSimpleWithComment() {
		
		scenarioProcessor.parse(
				SIMPLE_SCENARIO_WITH_COMMENT, storyToPopulate );
		
		List<Scenario> scenarios = storyToPopulate.getScenarios();
		
		assertTrue(scenarios.size() == 1);
		Scenario scenario = scenarios.get(0);
		assertEquals("Simple Scenario", scenario.getName());

		assertTrue(scenario.getGivens().size() == 1);
		assertEquals("I have a simple scenario text", scenario.getGivens().get(0));
		assertEquals("I parse it using Gherkin", scenario.getWhens().get(0));
		assertEquals("I get a scenario in Corejet object format", scenario.getThens().get(0));
	}
	
	@Test
	public void parseWithAnd() {
		
		scenarioProcessor.parse(
				SCENARIO_WITH_AND, storyToPopulate );
		
		List<Scenario> scenarios = storyToPopulate.getScenarios();
		
		Scenario scenario = scenarios.get(0);

		assertTrue(scenario.getGivens().size() == 2);
		assertTrue(scenario.getThens().size() == 3);
		assertEquals("I get a scenario in Corejet object format", scenario.getThens().get(0));
		assertEquals("there is more than one Given", scenario.getThens().get(1));
	}
	
	@Test
	public void testFilterConfluenceWikiMarkup() {
		GherkinScenarioProcessor scenarioProcessor = new GherkinScenarioProcessor();

		scenarioProcessor.parse("{code}" + SIMPLE_SCENARIO + "\n\n{code}", storyToPopulate);
		
		List<Scenario> scenarios = storyToPopulate.getScenarios();
		Scenario scenario = scenarios.get(0);
		assertEquals("I have a simple scenario text", scenario.getGivens().get(0));
	}
}
