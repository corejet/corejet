package org.corejet.pageobject.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.corejet.testrunner.parameters.internal.Resolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * Specifies that a field is a {@link ElementLocatorFactory} suitable for use in
 * constructing page objects via the
 * {@link PageFactory#initElements(ElementLocatorFactory, Object)} method.
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
