package org.corejet.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.corejet.repository.StoryRepository;

/**
 * Specifies the {@link StoryRepository} which should be used for looking up the
 * specifications of Stories.
 * 
 * @author rnorth
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ ElementType.TYPE })
public @interface StorySource {

	Class<? extends StoryRepository> value();
}
