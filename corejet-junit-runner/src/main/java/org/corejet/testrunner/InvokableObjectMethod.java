package org.corejet.testrunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A convenience class used to provide both an object and method reference.
 * Enables the direct invocation of any given method on any given instance.
 * 
 * Not for reuse.
 * 
 * @author rnorth
 * 
 */
class InvokableObjectMethod {

	public InvokableObjectMethod(Object object, Method method) {
		this.object = object;
		this.method = method;
	}

	private Object object;
	private Method method;

	/**
	 * Invoke the method on the target object, without attempting any
	 * modifications to method accessibility.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void invoke(Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		method.invoke(object, args);
	}

	/**
	 * @return expected parameter types for the method.
	 */
	public Class<?>[] getParamTypes() {
		return method.getParameterTypes();
	}
}
