package org.corejet.testrunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.time.StopWatch;
import org.corejet.annotations.CopyFromParent;
import org.corejet.annotations.Given;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.model.Failure;
import org.corejet.model.Scenario;
import org.corejet.testrunner.parameters.ParameterUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

/**
 * {@link CoreJetFrameworkMethod} serves as a synthetic JUnit test method, which
 * coordinates the execution of a CoreJet scenario. When this class'
 * {@link CoreJetFrameworkMethod#invoke} method is called, the appropriate
 * Given, When and Then methods will be fired, in accordance with the Scenario
 * script.
 * 
 * @author rnorth, rpickard
 * 
 */
public class CoreJetFrameworkMethod extends FrameworkMethod {

	/**
	 * An instance of the class annotated @Scenario which this FrameworkMethod
	 * corresponds to.
	 */
	private Object instance;

	/**
	 * The class of the instance, i.e. the class which has been annotated with
	 * 
	 * @Scenario.
	 */

	private Class<?> scenarioClass;

	/**
	 * The {@link Scenario} data object (populated from a Story source such as
	 * JIRA) which this FrameworkMethod is based upon.
	 */
	private Scenario scenario;

	/**
	 * The instance of the parent class of 'instance' (i.e. the outer,
	 * 
	 * @Story-annotated class).
	 */
	private Object parentInstance;

	private static final Logger testProgressLogger = LoggerFactory.getLogger("corejet");

	/**
	 * Standard constructor (required).
	 * 
	 * @param method
	 */
	public CoreJetFrameworkMethod(Method method) {
		super(method);
	}

	/**
	 * @param scenarioClass
	 *            the Class which is annotated with @Scenario
	 * @param scenario
	 *            the {@link Scenario} which this class corresponds with
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public CoreJetFrameworkMethod(Class<?> scenarioClass, Scenario scenario) throws CorejetFrameworkMethodException, SecurityException, NoSuchMethodException {

		/*
		 * Register this class' invoke method to be called by the parent
		 * FrameworkMethod class when the 'test' is to be run.
		 */
		super(CoreJetFrameworkMethod.class.getMethod("invoke"));

		this.scenarioClass = scenarioClass;
		this.scenario = scenario;

		try {
			instance = scenarioClass.newInstance();
		} catch (InstantiationException e) {
			throw new CorejetFrameworkMethodException("Could not instantiate scenario class " + scenarioClass, e);
		} catch (IllegalAccessException e) {
			throw new CorejetFrameworkMethodException("Could not instantiate scenario class " + scenarioClass, e);
		}
	}

	/**
	 * Prepares the invocation of the necessary Given, When and Then methods for
	 * this Scenario.
	 * 
	 * @return
	 */
	public Statement invoke() {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {

				// Associate @SameAsParent fields with the parent instance's
				// values
				associateSameAsParentFields(instance, parentInstance);

				// Scan scenario level methods first, followed by parent class
				scanAndInvokeGivensWhensThensForInstance(instance, parentInstance);
			}

		};
	}

	/**
	 * For any field annotated @SameAsParent in the Scenario, set the value to
	 * be the same as an identically-named field on the parent class. If the
	 * parent class does not have an identically-named field, it's ancestor
	 * classes will be examined and used instead.
	 * 
	 * This should work with fields of any scope (including private).
	 * 
	 * @param instance
	 * @param parentInstance
	 */
	protected void associateSameAsParentFields(Object instance, Object parentInstance) {

		Field[] childInstanceFields = instance.getClass().getDeclaredFields();

		for (Field childField : childInstanceFields) {
			if (childField.getAnnotation(CopyFromParent.class) != null) {

				// Try and find the same field in the parent instance
				try {
					Field parentField = getFieldFromParentHierarchy(parentInstance, childField.getName());
					parentField.setAccessible(true);
					Object parentValue = parentField.get(parentInstance);
					parentField.setAccessible(false);

					childField.setAccessible(true);
					childField.set(instance, parentValue);
					childField.setAccessible(false);

				} catch (SecurityException e) {
					throw new CoreJetJunitTestRunnerException("Could not map @SameAsParent field", e);
				} catch (IllegalArgumentException e) {
					throw new CoreJetJunitTestRunnerException("Could not map @SameAsParent field", e);
				} catch (IllegalAccessException e) {
					throw new CoreJetJunitTestRunnerException("Could not map @SameAsParent field", e);
				} catch (NoSuchFieldException e) {
					throw new CoreJetJunitTestRunnerException("Expected to find a field named " + childField.getName()
							+ " on parent test class " + parentInstance.getClass() + " due to use of @SameAsParent");
				}
			}
		}

	}

	/**
	 * Recurse through parentInstance's class and parent classes until a field
	 * named fieldName is found.
	 * 
	 * @param parentInstance
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 *             if no parent class has an identically-named field
	 */
	private Field getFieldFromParentHierarchy(Object parentInstance, String fieldName) throws NoSuchFieldException {

		Class<?> classToExamine = parentInstance.getClass();
		while (classToExamine.getSuperclass() != null) {

			try {
				return classToExamine.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Suppress
				classToExamine = classToExamine.getSuperclass();
			}
		}

		throw new NoSuchFieldException("Could not find field named:" + fieldName + " on " + parentInstance.getClass() + " or superclasses");
	}

	/**
	 * For each Given, When, Then in the Scenario specification, find
	 * corresponding annotated methods on the scenario instance, and invoke in
	 * sequence. If matching annotated methods are not found on the scenario
	 * instance, the story parent class is used as a fallback (e.g. a
	 * commonly-used @Given would be created at Story level rather than repeated
	 * in each scenario class).
	 * 
	 * @param instance
	 * @param parentInstance
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws FailedMethodError 
	 */
	protected void scanAndInvokeGivensWhensThensForInstance(Object instance, Object parentInstance) throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException, SecurityException, NoSuchFieldException, FailedMethodError {

		/*
		 * TODO: Implementation is a bit repetitive and should be tidied up.
		 */
		for (String givenMethodName : scenario.getGivens().keySet()) {
			InvokableObjectMethod invokable = findGivenMethodByStepName(givenMethodName, instance);
			if (invokable == null) {
				invokable = findGivenMethodByStepName(givenMethodName, parentInstance);
			}

			if (invokable == null) {
				throw new CoreJetJunitTestRunnerException("Could not find a @Given(\"" + givenMethodName + "\") for scenario "
						+ instance.getClass());
			}

			testProgressLogger.info("  Given: " + givenMethodName);

			invokeStepMethod(invokable, givenMethodName, instance, parentInstance);
		}

		for (String whenMethodName : scenario.getWhens().keySet()) {
			InvokableObjectMethod invokable = findWhenMethodByStepName(whenMethodName, instance);
			if (invokable == null) {
				invokable = findWhenMethodByStepName(whenMethodName, parentInstance);
			}

			if (invokable == null) {
				throw new CoreJetJunitTestRunnerException("Could not find a @When(\"" + whenMethodName + "\") for scenario "
						+ instance.getClass());
			}

			testProgressLogger.info("  When: " + whenMethodName);

			invokeStepMethod(invokable, whenMethodName, instance, parentInstance);
		}

		for (String thenMethodName : scenario.getThens().keySet()) {
			InvokableObjectMethod invokable = findThenMethodByStepName(thenMethodName, instance);
			if (invokable == null) {
				invokable = findThenMethodByStepName(thenMethodName, parentInstance);
			}

			if (invokable == null) {
				throw new CoreJetJunitTestRunnerException("Could not find a @Then(\"" + thenMethodName + "\") for scenario "
						+ instance.getClass());
			}

			testProgressLogger.info("  Then: " + thenMethodName);

			invokeStepMethod(invokable, thenMethodName, instance, parentInstance);
		}
	}

	/**
	 * Invoke a step method, resolving its parameters just prior to invocation.
	 * 
	 * @param invokable
	 * @param stepName
	 * @param instance
	 * @param parentInstance3
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws FailedMethodError 
	 */
	private void invokeStepMethod(InvokableObjectMethod invokable, String stepName, Object instance, Object parentInstance3) throws IllegalAccessException, InvocationTargetException, SecurityException, IllegalArgumentException, NoSuchFieldException, FailedMethodError {

		Object[] args = {};
		Class<?>[] paramTypes = invokable.getParamTypes();
		if (paramTypes.length > 0) {
			args = ParameterUtils.resolveStepParameters(stepName, instance, parentInstance3, paramTypes);
		}

		try {
			// time and record each step
			StopWatch watch = new StopWatch();
			watch.start();
			invokable.invoke(args);
			watch.stop();
			if (!setTime(stepName, scenario.getGivens(), watch)){
				if (!setTime(stepName, scenario.getWhens(), watch)){
					setTime(stepName, scenario.getThens(), watch);
				}
			}
		} catch (Exception e) {
			scenario.setFailure(new Failure(stepName, e));
			testProgressLogger.error("Failed at step: "+stepName);
			throw new FailedMethodError(e);
		}
	}

	private boolean setTime(String stepName, Map<String, Double> steps, StopWatch watch) {
		for (Entry<String, Double> then : steps.entrySet()){
			if (then.getKey().equals(stepName)){
				then.setValue(Double.parseDouble(Long.toString(watch.getTime()))/1000);
				return true;
			}
		}
		return false;
	}

	/**
	 * Try and find a matching Given method on the instance passed in - i.e. one
	 * where there is a @Given annotation whose text matches that of the Given
	 * in the scenario specification.
	 * 
	 * @param testMethodName
	 * @param instance
	 * @return
	 */
	protected InvokableObjectMethod findGivenMethodByStepName(String testMethodName, Object instance) {
		Method[] availableMethods = instance.getClass().getMethods();
		// Check scenario class first
		for (Method m : availableMethods) {
			Given annotation = m.getAnnotation(Given.class);

			if (annotation != null && testMethodName.trim().equals(annotation.value().trim())) {
				return new InvokableObjectMethod(instance, m);
			}
		}
		return null;
	}

	/**
	 * Try and find a matching When method on the instance passed in - i.e. one
	 * where there is a @When annotation whose text matches that of the Given
	 * in the scenario specification.
	 * 
	 * @param testMethodName
	 * @param instance
	 * @return
	 */
	protected InvokableObjectMethod findWhenMethodByStepName(String testMethodName, Object instance) {
		Method[] availableMethods = instance.getClass().getMethods();
		for (Method m : availableMethods) {
			When annotation = m.getAnnotation(When.class);

			if (annotation != null && testMethodName.trim().equals(annotation.value().trim())) {
				return new InvokableObjectMethod(instance, m);
			}
		}
		return null;
	}

	/**
	 * Try and find a matching Then method on the instance passed in - i.e. one
	 * where there is a @Then annotation whose text matches that of the Given
	 * in the scenario specification.
	 * 
	 * @param testMethodName
	 * @param instance
	 * @return
	 */
	protected InvokableObjectMethod findThenMethodByStepName(String testMethodName, Object instance) {
		Method[] availableMethods = instance.getClass().getMethods();
		for (Method m : availableMethods) {
			Then annotation = m.getAnnotation(Then.class);

			if (annotation != null && testMethodName.trim().equals(annotation.value().trim())) {
				return new InvokableObjectMethod(instance, m);
			}
		}
		return null;
	}

	/**  
	 * @{inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CoreJetFrameworkMethod) {
			final CoreJetFrameworkMethod other = (CoreJetFrameworkMethod) obj;
			return Objects.equal(scenarioClass, other.scenarioClass);
		} else {
			return false;
		}
	}

	/**  
	 * @{inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(scenarioClass);
	}

	/**  
	 * @{inheritDoc}
	 */
	@Override
	public String toString() {
		return scenario.getName();
	}

	public Object getParentInstance() {
		return parentInstance;
	}

	public void setParentInstance(Object parentInstance) {
		this.parentInstance = parentInstance;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public Class<?> getScenarioClass() {
		return scenarioClass;
	}
}
