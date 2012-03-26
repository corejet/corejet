
package org.corejet.maven.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;

import org.apache.commons.lang.WordUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.corejet.OnlineJiraStoryRepository;
import org.corejet.annotations.Given;
import org.corejet.annotations.StorySource;
import org.corejet.annotations.Then;
import org.corejet.annotations.When;
import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Scenario;
import org.corejet.model.Story;
import org.corejet.model.exception.ParsingException;
import org.corejet.testrunner.CoreJetTestRunner;
import org.junit.runner.RunWith;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;

/**
 * Generate Java skeleton corejet test files for a requirements catalogue.
 * 
 * @goal generate-test-skeleton
 * 
 * @author rnorth
 * 
 */
public class TestSkeletonGeneratorMojo extends AbstractMojo {

	private static final String ANNOTATION_VALUE_PARAM = "value";

	/**  
	 * @{inheritDoc}
	*/
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		int generatedClassCount = 0;
		
		try {
			RequirementsCatalogue requirementsCatalogue = loadRequirementsCatalogue();
			
			for (Epic epic : requirementsCatalogue.getEpics()) {
				getLog().info(" - Processing epic: " + epic.getTitle());
				
				for (Story story : epic.getStories()) {
					getLog().info(" -- Processing story: " + story.getTitle());
					
					generateStoryTestClass(story);
					generatedClassCount++;
				}
			}
			
		} catch (ParsingException e) {
			throw new MojoExecutionException("Could not parse requirements!", e);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not read/write file", e);
		}
		
		getLog().info("Generated " + generatedClassCount + " classes in " + generatedSourcesOutputDirectory);
	}

	/**
	 * Create a Java class source file for a given Story.
	 * 
	 * @param story
	 * @throws JClassAlreadyExistsException
	 * @throws IOException
	 */
	private void generateStoryTestClass(Story story) throws IOException {
		
		String cleanStoryName = cleanNameFor(story.getTitle());
		
		JCodeModel cm = new JCodeModel();
		JDefinedClass definedClass;
		String storyClassName = "corejet." + cleanStoryName + "Test";
		
		try {
			definedClass = cm._class(storyClassName);
		} catch (JClassAlreadyExistsException e) {
			getLog().error("Could not create class " + storyClassName + " due to JClassAlreadyExistsException", e);
			return;
		}
		
		JAnnotationUse storyAnnotation = definedClass.annotate(org.corejet.annotations.Story.class);
		storyAnnotation.param("id", story.getId());
		storyAnnotation.param("title", story.getTitle());
		annotateClass(definedClass, RunWith.class, CoreJetTestRunner.class);
		annotateClass(definedClass, StorySource.class, OnlineJiraStoryRepository.class);

		for (Scenario scenario : story.getScenarios()) {
			generateScenarioClass(definedClass, scenario);
		}
		
		generatedSourcesOutputDirectory.mkdirs();
		cm.build(generatedSourcesOutputDirectory, new PrintStream(new ByteArrayOutputStream()));
		
	}

	
	/**
	 * Create an inner scenario class within a story class.
	 * 
	 * @param parentStoryClass
	 * @param scenario
	 * @throws JClassAlreadyExistsException
	 */
	private void generateScenarioClass(JDefinedClass parentStoryClass, Scenario scenario) {
		
		String cleanScenarioName = cleanNameFor(scenario.getName() + "Scenario");
		
		JDefinedClass scenarioStaticInnerClass;
		try {
			scenarioStaticInnerClass = parentStoryClass._class(JMod.STATIC + JMod.PUBLIC, cleanScenarioName);
		} catch (JClassAlreadyExistsException e) {
			getLog().error("Could not create class " + cleanScenarioName + " due to JClassAlreadyExistsException", e);
			return;
		}
		annotateClass(scenarioStaticInnerClass, org.corejet.annotations.Scenario.class, scenario.getName());
		
		for (String given : scenario.getGivens().keySet()) {
			String cleanGivenName = cleanNameFor(given);
			JMethod method = scenarioStaticInnerClass.method(JMod.PUBLIC, void.class, cleanGivenName);
			annotateMethod(method, Given.class, given);
		}
		
		for (String when : scenario.getWhens().keySet()) {
			String cleanGivenName = cleanNameFor(when);
			JMethod method = scenarioStaticInnerClass.method(JMod.PUBLIC, void.class, cleanGivenName);
			annotateMethod(method, When.class, when);
		}
		
		for (String then : scenario.getThens().keySet()) {
			String cleanGivenName = cleanNameFor(then);
			JMethod method = scenarioStaticInnerClass.method(JMod.PUBLIC, void.class, cleanGivenName);
			annotateMethod(method, Then.class, then);
		}
	}

	private JAnnotationUse annotateMethod(JMethod method, Class<? extends Annotation> clazz, String value) {
		JAnnotationUse annotationUse = method.annotate(clazz);
		annotationUse.param(ANNOTATION_VALUE_PARAM, value);
		return annotationUse;
	}

	private JAnnotationUse annotateClass(JDefinedClass definedClass, Class<? extends Annotation> clazz, String value) {
		JAnnotationUse annotationUse = definedClass.annotate(clazz);
		annotationUse.param(ANNOTATION_VALUE_PARAM, value);
		return annotationUse;
	}
	
	private JAnnotationUse annotateClass(JDefinedClass definedClass, Class<? extends Annotation> clazz, Class value) {
		JAnnotationUse annotationUse = definedClass.annotate(clazz);
		annotationUse.param(ANNOTATION_VALUE_PARAM, value);
		return annotationUse;
	}

	/**
	 * Convert a phrase into something suitable for use as a class or method name.
	 * 
	 * @param title
	 * @return
	 */
	private String cleanNameFor(String title) {
		String titleWithoutPunctuation = title.replaceAll("\\p{Punct}", " ");
		String capitalizedTitleWithSpaces = WordUtils.capitalizeFully(titleWithoutPunctuation);
		
		return capitalizedTitleWithSpaces.replaceAll("\\s", "");
	}

	private RequirementsCatalogue loadRequirementsCatalogue() throws FileNotFoundException, ParsingException {
		
		getLog().info("Loading requirements catalogue from file " + requirementsDirectory);
		
		FileInputStream fis = new FileInputStream(requirementsDirectory);
		return new RequirementsCatalogue(fis);
	}

	/**
	 * Directory containing Corejet requirements catalogue / test output files.
	 * @parameter alias="requirementsDirectory"
	 * @required
	 */
	private File requirementsDirectory;
	
	/**
	 * Directory where generated sources should be placed.
	 * @parameter default-value="target/generated-sources/corejet"
	 */
	private File generatedSourcesOutputDirectory;
}
