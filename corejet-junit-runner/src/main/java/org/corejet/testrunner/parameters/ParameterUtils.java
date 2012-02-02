package org.corejet.testrunner.parameters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.corejet.testrunner.parameters.internal.Resolver;

/**
 * Utilities to resolve parameters for step methods.
 * 
 * @author rnorth
 * 
 */
public class ParameterUtils {

	/**
	 * Resolve parameters for step methods, scanning fields for annotations with
	 * a parameterResolver field of type {@link ParameterResolver}.
	 * 
	 * @param stepName
	 *            the text of the step, without prefixed Given/When/Then
	 * @param instance
	 *            the test instance (scenario level)
	 * @param parentInstance
	 *            the instance of the parent (story level)
	 * @param paramTypes
	 *            an array of the expected Classes of the parameters for this
	 *            step
	 * @return parameters to be used when invoking the step method
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object[] resolveStepParameters(String stepName, Object instance, Object parentInstance, Class<?>[] paramTypes)
			throws ParameterResolutionException {
		List<ParameterResolver> resolvers = new ArrayList<ParameterResolver>();

		try {
			resolvers.addAll(findResolversOnInstanceAnnotatedFields(instance));
		} catch (SecurityException e) {
			throw new ParameterResolutionException("Could not find parameter resolvers on " + instance, e);
		} catch (IllegalArgumentException e) {
			throw new ParameterResolutionException("Could not find parameter resolvers on " + instance, e);
		} catch (NoSuchFieldException e) {
			throw new ParameterResolutionException("Could not find parameter resolvers on " + instance, e);
		} catch (IllegalAccessException e) {
			throw new ParameterResolutionException("Could not find parameter resolvers on " + instance, e);
		}

		Object[] args = new Object[paramTypes.length];
		for (ParameterResolver resolver : resolvers) {
			resolver.resolve(stepName, instance, parentInstance, paramTypes, args);
		}

		return args;
	}

	/**
	 * Looks for fields on an instance, and checks any annotations on them for a
	 * public static field named parameterResolver of type {@link ParameterResolver}.
	 * 
	 * These parameter resolvers can then be used to determine correct
	 * parameters to a step method.
	 * 
	 * @param instance
	 * @return all found {@link ParameterResolver}s
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static Collection<? extends ParameterResolver> findResolversOnInstanceAnnotatedFields(Object instance)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		List<ParameterResolver> result = new ArrayList<ParameterResolver>();

		for (Field f : instance.getClass().getDeclaredFields()) {
			for (Annotation a : f.getAnnotations()) {
				try {
					Resolver resolverAnnotation = a.annotationType().getAnnotation(Resolver.class);

					if (resolverAnnotation != null) {
						result.add(resolverAnnotation.value().newInstance());
					}
				} catch (InstantiationException e) {
					throw new ParameterResolutionException("Could not instantiate resolver", e);
				}
			}
		}
		return result;
	}

}
