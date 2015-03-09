package org.corejet.maven.codegen;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Test;


public class MojoCodeGenerationTest {

	@Test
	public void simpleTest() throws MojoExecutionException, MojoFailureException, IllegalAccessException, IOException {
		TestSkeletonGeneratorMojo mojo = new TestSkeletonGeneratorMojo();
		
		File requirementsFile = new File("src/test/resources/corejet-requirements.xml");
		File outputDir = new File("src/test/resources/generated-by-test");
		outputDir.mkdirs();
		
		ReflectionUtils.setVariableValueInObject(mojo, "requirementsDirectory", requirementsFile);
		ReflectionUtils.setVariableValueInObject(mojo, "generatedSourcesOutputDirectory", outputDir);
		
		mojo.execute();
		
		assertTrue( new File(outputDir, "corejet").list().length == 2);
	}
}
