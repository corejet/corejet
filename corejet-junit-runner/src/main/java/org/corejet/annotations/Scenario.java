package org.corejet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a static inner class corresponding to a {@link org.corejet.model.Scenario}.
 * 
 * @author rnorth
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Scenario {

	String value();

}
