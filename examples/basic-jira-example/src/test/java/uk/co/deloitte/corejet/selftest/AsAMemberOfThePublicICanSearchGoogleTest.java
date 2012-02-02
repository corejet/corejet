package uk.co.deloitte.corejet.selftest;

import org.corejet.JiraStoryRepository;
import org.corejet.annotations.CopyFromParent;
import org.corejet.annotations.Given;
import org.corejet.annotations.Scenario;
import org.corejet.annotations.Story;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.pageobject.support.WebDriverPageProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import uk.co.deloitte.corejet.selftest.pageobjects.GoogleHomePage;
import uk.co.deloitte.corejet.selftest.pageobjects.GoogleSearchResultsPage;

@Story(id = "CTI-3", title = "As a member of the public I can search Google")
@RunWith(org.corejet.testrunner.CoreJetTestRunner.class)
@StorySource(JiraStoryRepository.class)
public class AsAMemberOfThePublicICanSearchGoogleTest {

	private WebDriver driver;

	@Before
	public void setup() {
		System.out.println("In setup()");
		driver = new HtmlUnitDriver();
	}

	@After
	public void teardown() {
		driver.close();
	}

	@Given("I am at the google homepage")
	public void IAmAtTheGoogleHomepage() {
		driver.get("http://www.google.com");
	}

	@Then("I am still at the google homepage")
	/*
	 * This method is only at story level to demonstrate that page object
	 * initialization works at both scenario and story level. It really belongs
	 * to the BlankSearch scenario.
	 */
	public void IAmTakenToTheSearchResultsPage(GoogleHomePage page) {
		page.assertContainsExpectedElements();
	}

	@Scenario("simple search")
	public static class SimpleSearch {

		@CopyFromParent
		@WebDriverPageProvider
		WebDriver driver;

		@When("I search for 'BBC News'")
		public void ISearchForBbcNews(GoogleHomePage page) {
			page.search("BBC News");
		}

		@Then("I am taken to the search results page")
		public void IAmTakenToTheSearchResultsPage(GoogleSearchResultsPage page) {
			page.assertContainsExpectedElements();
		}

		@Then("there is a link to 'BBC News'")
		public void ThereIsALinkToBbcNews(GoogleSearchResultsPage page) {
			page.hasLinkContaining("BBC News");
		}

	}

	@Scenario("blank search")
	public static class BlankSearch {

		@CopyFromParent
		@WebDriverPageProvider
		WebDriver driver;

		@When("I search for ''")
		public void ISearchForBbcNews(GoogleHomePage page) {
			page.search("");
		}

	}

}
