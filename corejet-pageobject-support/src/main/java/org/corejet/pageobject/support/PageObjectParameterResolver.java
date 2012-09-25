package org.corejet.pageobject.support;

import java.lang.reflect.Field;

import org.corejet.testrunner.parameters.ParameterResolutionException;
import org.corejet.testrunner.parameters.ParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * {@link ParameterResolver} which constructs any {@link PageObject} parameters.
 * 
 * @author rnorth
 * 
 */
public class PageObjectParameterResolver implements ParameterResolver {
	
	/**
	 * @{inheritDoc
	 */
	public void resolve(String stepName, Object instance, Object parentInstance, Class<?>[] paramTypes, Object[] existingArgs)
			throws ParameterResolutionException {

		WebDriver driver;
		try {
			driver = findDriverOnInstance(instance);
		} catch (IllegalAccessException e) {
			throw new ParameterResolutionException("Could not resolve WebDriver field on instance " + instance);
		}

		for (int i = 0; i < paramTypes.length; i++) {
			Class<?> clazz = paramTypes[i];
			if (PageObject.class.isAssignableFrom(clazz) && existingArgs[i] == null) {
				// is a page object, so try and initialize it
				Object resolvedArg = PageFactory.initElements(driver, clazz);
				existingArgs[i] = resolvedArg;
			}
		}

	}

	/**
	 * Locate a WebDriver on the test instance which has been annotated with
	 * 
	 * @WebDriverPageProvider.
	 * 
	 * @param instance
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static WebDriver findDriverOnInstance(Object instance) throws IllegalArgumentException, IllegalAccessException {
		for (Field f : instance.getClass().getDeclaredFields()) {
			if (f.getAnnotation(WebDriverPageProvider.class) != null && WebDriver.class.isAssignableFrom(f.getType())) {

				f.setAccessible(true);
				WebDriver driver = (org.openqa.selenium.WebDriver) f.get(instance);
				f.setAccessible(false);
				return driver;
			}
		}
		throw new ParameterResolutionException("Could not find expected WebDriver field marked @WebDriverPageProvider on instance: " + instance);
	}

}
