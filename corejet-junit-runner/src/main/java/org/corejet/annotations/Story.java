package org.corejet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a class as corresponding to a {@link org.corejet.model.Story}.
 * 
 * @author rnorth
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Story {

	/**
	 * The ID of the story, to be used for looking up in the requirements catalogue.
	 * 
	 * @return
	 */
	String id();

	/**
	 * The title of the story, currently for information only.
	 * 
	 * @return
	 */
	String title();

}
