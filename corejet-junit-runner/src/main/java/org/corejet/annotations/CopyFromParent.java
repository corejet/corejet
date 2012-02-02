package org.corejet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field which CoreJet should assist with wiring up between parent
 * (Story) instance and child (Scenario). Intended to be used to enable, for
 * example, common WebDriver instances to be shared between Given, When and Then
 * steps regardless of whether they appear in the Scenario class or as
 * 'inherited' methods in the Story class.
 * 
 * This copy is by reference, and one-way.
 * 
 * @author rnorth
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.FIELD })
public @interface CopyFromParent {

}
