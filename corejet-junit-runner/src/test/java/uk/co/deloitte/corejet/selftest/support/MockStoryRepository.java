package uk.co.deloitte.corejet.selftest.support;

import java.util.Map;

import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Scenario;
import org.corejet.model.Story;
import org.corejet.repository.StoryRepository;
import org.corejet.repository.exception.StoryRepositoryException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MockStoryRepository implements StoryRepository {

	Map<String,Story> stories = Maps.newHashMap();
	RequirementsCatalogue requirementsCatalogue = new RequirementsCatalogue();
	{{
		Scenario blah = new Scenario();
		blah.setName("blah");
		blah.addGiven("given");
		blah.addWhen("when");
		blah.addThen("then");
		
		Scenario wah = new Scenario();
		wah.setName("wah");
		wah.addGiven("PARENT given");
		wah.addWhen("when");
		wah.addThen("then");
		
		Scenario foo = new Scenario();
		foo.setName("foo");
		foo.addGiven("PARENT given");
		foo.addWhen("when");
		foo.addThen("then");
		
		Scenario bar = new Scenario();
		bar.setName("bar");
		bar.addGiven("PARENT given");
		bar.addWhen("when");
		bar.addThen("then");
		
		Story story1 = new Story();
		story1.setTitle("SimpleBddTestCase");
		story1.setId("ID-1");
		story1.getScenarios().add(blah);
		story1.getScenarios().add(wah);
		stories.put("ID-1", story1);
		
		
		blah.setParentStory(story1);
		wah.setParentStory(story1);
		
		Story story2 = new Story();
		story2.setTitle("SimpleBddTestCase2");
		story2.setId("ID-2");
		story2.getScenarios().add(foo);
		story2.getScenarios().add(bar);
		stories.put("ID-2", story2);
		
		foo.setParentStory(story2);
		bar.setParentStory(story2);

		
		Epic epic1 = new Epic();
		epic1.setId("EPIC-1");
		epic1.setTitle("Epic Title");
		epic1.setStories(Lists.newArrayList(stories.values()));
		
		requirementsCatalogue.setEpics(Lists.newArrayList(epic1));
		requirementsCatalogue.setProject("Simple BDD Project");
	}}
	
	public Map<String, Story> getAllStories() throws StoryRepositoryException {
		return stories;
	}

	public RequirementsCatalogue getRequirementsCatalogue() {
		return requirementsCatalogue;
	}

	
}
