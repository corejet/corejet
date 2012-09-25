package org.corejet.testrunner;

import java.lang.reflect.Method;

import org.corejet.model.Failure;
import org.corejet.model.Scenario;
import org.corejet.pageobject.support.PageObjectParameterResolver;
import org.corejet.pageobject.support.PageObjectSupport;
import org.openqa.selenium.WebDriver;

/**
 * Selenium specific implementation to add screen shot links for failed tests
 * @author rpickard
 *
 */
public class CoreJetSeleniumFrameworkMethod extends CoreJetFrameworkMethod{

	public CoreJetSeleniumFrameworkMethod(Method method) {
		super(method);
	}

	public CoreJetSeleniumFrameworkMethod(Class<?> scenarioInnerClass, Scenario scenario) throws CorejetFrameworkMethodException, SecurityException, NoSuchMethodException {
		super(scenarioInnerClass, scenario);
	}

	/* 
	 * Overwritten to store a link to the screenshot if available
	 */
	@Override
	protected void storeFailure(String stepName, Exception e) {
		String screenShot=null;
		try {
			WebDriver driver = PageObjectParameterResolver.findDriverOnInstance(instance);
			screenShot = PageObjectSupport.getScreenShot(driver);
		} catch (Exception e1) {
			System.out.println(e1.getCause());
		} 
		screenShot = screenShot.substring(screenShot.indexOf("/screenshots/")+1);
		scenario.setFailure(new Failure(stepName, e,screenShot));
	}
}
