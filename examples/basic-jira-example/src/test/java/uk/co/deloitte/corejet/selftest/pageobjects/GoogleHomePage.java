package uk.co.deloitte.corejet.selftest.pageobjects;

import org.corejet.pageobject.support.PageObject;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GoogleHomePage implements PageObject {

	@FindBy(name="q")
	private WebElement queryBox;
	
	@FindBy(name="btnG")
	private WebElement searchButton;
	
	private WebDriver driver;
	
	public GoogleHomePage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void search(String string) {
		queryBox.sendKeys(string);
		searchButton.click();
	}

	public void assertContainsExpectedElements() {
		Assert.assertEquals("Google", driver.getTitle());
	}

}
