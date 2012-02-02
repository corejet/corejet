package uk.co.deloitte.corejet.selftest.pageobjects;


import org.corejet.pageobject.support.PageObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class GoogleSearchResultsPage implements PageObject {

	private WebDriver driver;
	
	public GoogleSearchResultsPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void assertContainsExpectedElements() {
		Assert.assertTrue( driver.getTitle().endsWith("Google Search"));
	}

	public void hasLinkContaining(String string) {
		WebElement link = driver.findElement(By.partialLinkText(string));
		
		Assert.assertTrue(link != null);
	}

}
