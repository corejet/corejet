package org.corejet.pageobject.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.corejet.testrunner.parameters.internal.Resolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Specifies that a field is a {@link WebDriver} suitable for use in
 * constructing page objects via the
 * {@link PageFactory#initElements(WebDriver, Class)} method.
 * 
 * @author rnorth
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Resolver(PageObjectParameterResolver.class)
public @interface ElementLocatorFactoryProvider {

}
