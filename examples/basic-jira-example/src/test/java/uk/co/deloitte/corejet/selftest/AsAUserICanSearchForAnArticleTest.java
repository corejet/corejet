
package uk.co.deloitte.corejet.selftest;

import org.corejet.annotations.Given;
import org.corejet.annotations.Scenario;
import org.corejet.annotations.Story;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.junit.runner.RunWith;

@Story(id = "CTI-2", title = "Changed As a user I can search for an article")
@RunWith(org.corejet.testrunner.CoreJetTestRunner.class)
@StorySource(org.corejet.JiraStoryRepository.class)
public class AsAUserICanSearchForAnArticleTest {


    @Scenario("Search for an existing article with exactly one match")
    public static class SearchForAnExistingArticleWithExactlyOneMatch {


        @Given("I am logged in to the system as a registered user")
        public void IAmLoggedInToTheSystemAsARegisteredUser() {
        }

        @Given("there is exactly one article with the word 'baz' in its title")
        public void ThereIsExactlyOneArticleWithTheWordBazInItsTitle() {
        }

        @When("I type 'baz' into a search box")
        public void ITypeBazIntoASearchBox() {
        }

        @Then("I am shown the article in full")
        public void IAmShownTheArticleInFull() {
        }

    }

    @Scenario("Search for an existing article with multiple matches")
    public static class SearchForAnExistingArticleWithMultipleMatches {


        @Given("I am logged in to the system as a registered user")
        public void IAmLoggedInToTheSystemAsARegisteredUser() {
        }

        @Given("there are three articles with the word 'foo' in their titles")
        public void ThereAreThreeArticlesWithTheWordFooInTheirTitles() {
        }

        @When("I type 'foo' into a search box")
        public void ITypeFooIntoASearchBox() {
        }

        @Then("I am shown a list of search results with three matches")
        public void IAmShownAListOfSearchResultsWithThreeMatches() {
        }

        @Then("I can see the first 10 words of each article as a summary")
        public void ICanSeeTheFirst10WordsOfEachArticleAsASummary() {
        }

    }

    @Scenario("Search with no matches")
    public static class SearchWithNoMatches {


        @Given("I am logged in to the system as a registered user")
        public void IAmLoggedInToTheSystemAsARegisteredUser() {
        }

        @Given("there are no articles with the word 'bar' in their titles")
        public void ThereAreNoArticlesWithTheWordBarInTheirTitles() {
        }

        @When("I type 'bar' into a search box")
        public void ITypeBarIntoASearchBox() {
        }

        @Then("I am shown a search results page with no matches")
        public void IAmShownASearchResultsPageWithNoMatches() {
        }

        @Then("the page contains a warning that no results were found")
        public void ThePageContainsAWarningThatNoResultsWereFound() {
        }

    }

}
