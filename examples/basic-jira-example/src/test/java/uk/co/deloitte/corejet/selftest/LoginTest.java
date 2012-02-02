package uk.co.deloitte.corejet.selftest;

import static org.junit.Assert.assertTrue;

import org.corejet.OnlineJiraStoryRepository;
import org.corejet.annotations.Given;
import org.corejet.annotations.CopyFromParent;
import org.corejet.annotations.Scenario;
import org.corejet.annotations.Story;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.pageobject.support.WebDriverPageProvider;
import org.corejet.testrunner.CoreJetTestRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.corejet.JiraStoryRepository;

import uk.co.deloitte.corejet.selftest.pageobjects.HomePage;

@RunWith(CoreJetTestRunner.class)
@Story(id = "CTI-1", title = "As a user I can log in to the System")
@StorySource(JiraStoryRepository.class)
public class LoginTest extends ParentWithSharedField {

	@WebDriverPageProvider WebDriver pretendDriver = new HtmlUnitDriver();
	
	@Given("there is a user 'joebloggs' with password 'password'")
	public void userOnLoginPageAndAccountIsLocked() {

	}
	
	@Scenario("Changed User successful login")
	public static class LoginSuccess {
		
		@CopyFromParent @WebDriverPageProvider WebDriver pretendDriver = null;	/* Field on direct parent */
		@CopyFromParent Object superParentSharedField; /* Field on parent's parent */

		@When("I try to log in with username 'joebloggs' and password 'password'")
		public void enterCredentials() {
			
		}
		
		@Then("the system grants me access")
		public void accessIsGranted(HomePage page) {
			assertTrue(pretendDriver != null);
			assertTrue(superParentSharedField != null);
			assertTrue(page != null);
			System.out.println(page);
		}
		
		@Then("I am taken to a home page")
		public void takenToHomePage() {
			
		}
	}

	@Scenario("Failed login")
	public static class LoginPasswordFail {

		@CopyFromParent Object pretendDriver = null;
		
		@When("I try to log in with username 'joebloggs' and password 'badpassword'")
		public void enterBadCredentials() {
			
		}

		@Then("the system does not grant me access")
		public void accessNotGranted() {
			
		}
		
		@Then("I am taken back to the login page")
		public void takenToLoginPage() {
			
		}
		
		@Then("I am shown an error message indicating that my username or password was incorrect")
		public void shownCredentialsErrorMessage() {
			
		}
	}

}