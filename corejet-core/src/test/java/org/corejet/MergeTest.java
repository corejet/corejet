/**
 * 
 */
package org.corejet;

import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.exception.MergeException;
import org.corejet.model.exception.ParsingException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author rpickard
 *
 */
public class MergeTest {

	@Test
	public void testMerge() throws ParsingException, MergeException{
		RequirementsCatalogue catA = new RequirementsCatalogue(ClassLoader.getSystemResourceAsStream("corejet-requirements-a.xml"));
		RequirementsCatalogue catB = new RequirementsCatalogue(ClassLoader.getSystemResourceAsStream("corejet-requirements-b.xml"));
		
		RequirementsCatalogue result = RequirementsCatalogue.merge(catA, catB);

		Assert.assertEquals(catB.getExtractTime(),result.getExtractTime());
		Assert.assertEquals(catB.getEpics().get(0).getStories().get(0).getScenarios().get(0).getStatus(), result.getEpics().get(0).getStories().get(0).getScenarios().get(0).getStatus());	
		Assert.assertNotNull(result.getEpics().get(0).getStories().get(1).getScenarios().get(2).getFailure());
		Assert.assertEquals(catA.getEpics().get(0).getStories().get(0).getScenarios().get(1).getStatus(), result.getEpics().get(0).getStories().get(1).getScenarios().get(0).getStatus());	
		Assert.assertEquals(catA.getEpics().get(0).getStories().get(1).getScenarios().get(3).getGivens().values().iterator().next(),result.getEpics().get(0).getStories().get(1).getScenarios().get(3).getGivens().values().iterator().next());
	}
}
