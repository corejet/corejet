package org.corejet.testrunner.parameters;


public interface ParameterResolver {

	/**
	 * Resolve parameters for a given, when or then step.
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
	 * @param args
	 *            an array of arguments; null if not yet found, non-null if
	 *            found by a previous resolver. The {@link ParameterResolver}
	 *            should populate this array where possible.
	 * @throws ParameterResolutionException
	 */
	void resolve(String stepName, Object instance, Object parentInstance, Class<?>[] paramTypes, Object[] args)
			throws ParameterResolutionException;

}
