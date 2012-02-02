package uk.co.deloitte.corejet.selftest;

import static org.junit.Assert.assertEquals;

import org.corejet.annotations.Given;
import org.corejet.annotations.NotImplementedYet;
import org.corejet.annotations.Scenario;
import org.corejet.annotations.Story;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.testrunner.CoreJetTestRunner;
import org.corejet.testrunner.ParallelCoreJetTestRunner;
import org.junit.runner.RunWith;

import uk.co.deloitte.corejet.selftest.support.MockStoryRepository;

@RunWith(ParallelCoreJetTestRunner.class)
@StorySource(MockStoryRepository.class)
@Story(id="ID-2", title="SimpleBddTestCase2")
public class ParallelSimpleBddTest {

	protected final static String sharedField = "FOO";
	
	@Scenario("foo")
	public static class ScenarioFoo {
		
		@Given("given")
		public void myFirstGiven() {
			System.out.println("First Given hit");
		}
		
		@When("when")
		public void myFirstWhen() {
			System.out.println("First When hit");
		}
		
		@Then("then")
		public void myFirstThen() {
			System.out.println("First Then hit");
		}
	}
	
	@Given("PARENT given")
	public void mySecondGiven() {
		System.out.println("PARENT Given hit");
	}
	
	@Scenario("bar")
	public static class ScenarioBar {
		
		@When("when")
		public void mySecondWhen() {
			System.out.println("Second When hit");
		}
		
		@Then("then")
		public void mySecondThen() {
			System.out.println("Second Then hit");
			
			assertEquals("FOO", sharedField);
		}
	}
}
