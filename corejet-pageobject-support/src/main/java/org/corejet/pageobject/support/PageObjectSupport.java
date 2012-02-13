package org.corejet.pageobject.support;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Helper methods to speed up page object development.  Extend this class to create PageObjects
 * @author Developer
 *
 */
public class PageObjectSupport implements PageObject {

	public PageObjectSupport(WebDriver driver) {
		this.driver = driver;
	}

	private static final Random random = new Random();
	private int defaultWaitSeconds = 5;

	protected WebDriver driver;
	
	public int getDefaultWaitSeconds() {
		return defaultWaitSeconds;
	}

	public void setDefaultWaitSeconds(int defaultWaitSeconds) {
		this.defaultWaitSeconds = defaultWaitSeconds;
	}



	/**
	 * Wait for the given element to appear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected WebElement waitForElement(By by){
		return waitForElement(by, defaultWaitSeconds);
	}

	/**
	 * Wait for the given list of elements to appear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected List<WebElement> waitForElements(By by){
		return waitForElements(by, defaultWaitSeconds);
	}

	/**
	 * Wait for the given element to appear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected WebElement waitForElement(WebElement element){
		return waitForElement(element, 5);
	}

	/**
	 * Wait for the given element to appear
	 * @param by the locator for a 
	 */
	protected WebElement waitForElement(WebElement element, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		for (int i=0; i<loops; i++){
			try {				
				if (element.isDisplayed()){
					return element;
				}

				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				// try again
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e1) {
					// try again;
				}
			}
		}
		return null;
	}
	/**
	 * Wait for the given element to appear
	 * @param by the locator for a 
	 */
	protected WebElement waitForElement(By by, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		if (0==loops){
			loops=1;
		}
		for (int i=0; i<loops; i++){
			try {
				List<WebElement> elements = driver.findElements(by);
				for (WebElement element : elements){
					if (element.isDisplayed()){
						return element;
					}
				}
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				// try again
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e1) {
					// try again;
				}
			}
		}
		return null;
	}

	/**
	 * Wait for the given element to appear
	 * @param by the locator for a 
	 */
	protected List<WebElement> waitForElements(By by, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		if (0==loops){
			loops=1;
		}
		for (int i=0; i<loops; i++){
			try {
				List<WebElement> elements = driver.findElements(by);
				for (WebElement element : elements){
					if (element.isDisplayed()){
						return elements;
					}
				}
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				// try again
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e1) {
					// try again;
				}
			}
		}
		return null;
	}

	/**
	 * Wait for the given element to disappear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected void waitForElementToDisappear(WebElement element){
		waitForElementToDisappear(element, 5);
	}

	/**
	 * Wait for the given element to disappear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected void waitForElementToDisappear(By by){
		waitForElementToDisappear(by, 5);
	}

	/**
	 * Wait for the given element to disappear
	 * @param by the locator for a 
	 */
	protected void waitForElementToDisappear(By by, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		for (int i=0; i<loops; i++){
			try {
				WebElement element = null;
				List<WebElement> elements = driver.findElements(by);
				for (WebElement e : elements){
					if (e.isDisplayed()){
						element = e;
					}
				}
				if (!element.isDisplayed()){
					return;
				}
				try {
					Thread.sleep(500L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				return;
			}
		}
	}

	/**
	 * Wait for the given element to disappear
	 * @param by the locator for a 
	 */
	protected boolean waitForElementToDisappear(WebElement element, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		for (int i=0; i<loops; i++){
			try {
				if (!element.isDisplayed()){
					return true;
				}
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				return true;
			}
		} 
		return false;
	}

	protected void populateField(WebElement field, String value){
		waitForElement(field);
		field.click();
		field.clear();
		field.sendKeys(value);
		field.sendKeys(Keys.TAB.toString());
	}	


	protected boolean isImageLoaded(WebElement img){
		return (Boolean) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].complete", img);
	}

	protected boolean isElementDisplayed(WebElement element) {
		return (element != null && element.isDisplayed());
	}

	protected boolean isElementEnabled(WebElement element) {
		if (isElementDisplayed(element))
			return element.isEnabled();
		return false;
	}

	/**
	 * Wait for the given element to appear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected WebElement waitForElementInside(WebElement parent, By by){
		return waitForElementInside(parent,by, 10);
	}

	/**
	 * Wait for the given element to appear for up to 5 seconds
	 * @param by the locator for a 
	 */
	protected List<WebElement> waitForElementsInside(WebElement parent, By by){
		return waitForElementsInside(parent,by, 5);
	}


	/**
	 * Wait for the given element to appear
	 * @param by the locator for a 
	 */
	protected WebElement waitForElementInside(WebElement parent, By by, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		if (loops==0){
			loops = 1;
		}
		for (int i=0; i<loops; i++){
			try {
				List<WebElement> elements = parent.findElements(by);
				for (WebElement element : elements){
					if (element.isDisplayed()){
						return element;
					}
				}
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				// try again
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e1) {
					// try again;
				}
			}
		}
		return null;
	}

	/**
	 * Wait for the given element to appear
	 * @param by the locator for a 
	 */
	protected List<WebElement> waitForElementsInside(WebElement parent, By by, int maxTimeSeconds){
		int loops = maxTimeSeconds*4;
		if (loops==0){
			loops = 1;
		}
		for (int i=0; i<loops; i++){
			try {
				List<WebElement> elements = parent.findElements(by);
				for (WebElement element : elements){
					if (element.isDisplayed()){
						return elements;
					}
				}
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// try again;
				}
			} catch (Exception e) {
				// try again
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e1) {
					// try again;
				}
			}
		}
		return null;
	}

	/**
	 * Checks element exist
	 * @param by the locator for a 
	 */
	protected boolean elementExist(By by){
		try {
			driver.findElement(by);
			return true;
		}catch(org.openqa.selenium.NoSuchElementException Ex){
			return false;
		}
	}

	protected boolean elementExist(WebElement element){
		try {
			if (element.isDisplayed())
				return true;
			else
				return false;
		}catch(org.openqa.selenium.NoSuchElementException Ex){
			return false;
		}


	}

	protected void clickJsHrefLink(WebElement inputElement) {
		((JavascriptExecutor)driver).executeScript(inputElement.getAttribute("href").replace("javascript:", "").replace("%20", ""));
	}

	protected String getInnerHtml(WebElement element) {
		String text = ((JavascriptExecutor) driver).executeScript("return arguments[0].innerHTML", element).toString();
		text = text.replaceAll("\n","").replaceAll("\t", "").trim();
		return text;
	}

	protected String getNumberFromString(String input){
		return input.replaceAll("[^0-9.]+","");
	}

	/**
	 * get Page Title
	 * @return
	 */
	public String getPageTitle(){
		return driver.getTitle();
	}

	/**
	 * get New Email Address
	 * @return
	 */
	public String getNewEmailAddress() {
		StringBuffer sb = new StringBuffer("user");
		sb.append(Long.toString(random.nextInt()).replace("-", "")+(new Date()).getTime());
		sb.append("@corejet.org");
		return sb.toString();
	}

	public static Double parseDouble(String value){
		return Double.parseDouble(patternStrip(value, "[^0-9.]"));
	}
	
	public static Integer parseInteger(String value){
		return Integer.parseInt(patternStrip(value,"[^0-9]"));
	}

	private static String patternStrip(String value, String pattern) {
		return value.replaceAll(pattern, "");
	}
	

	public static <T> T findInList(String value, List<T> list){
		for(T item : list){
			if (item.toString().trim().contains(value.trim())){
				return item;
			}
		}
		return null;
	}
	
	public void clickBrowserBackButton(){
		driver.navigate().back();
	}
	

}
