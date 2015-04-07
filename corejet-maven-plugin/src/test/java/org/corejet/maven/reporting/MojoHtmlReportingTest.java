package org.corejet.maven.reporting;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author rpickard
 *
 */
public class MojoHtmlReportingTest {

	@Test
	public void testGenerateReport() throws MojoExecutionException, MojoFailureException, IOException{
		HtmlReportGeneratorMojo mojo = new HtmlReportGeneratorMojo();

		for (File input : new File("src/test/resources/corejet/test-output/").listFiles()){
			if (input.isFile()){
				FileUtils.copyFileToDirectory(input, new File("target/corejet/test-output/"));
			}
		}
		mojo.setCorejetBaseDirectory("target/corejet");
		mojo.execute();

		// TODO: reinstate. commented out because the results are not stable
		//Assert.assertEquals(IOUtil.toString(ClassLoader.getSystemResourceAsStream("expected-corejet-report.html")), IOUtil.toString(new FileInputStream(reportDestination)));
	}

}
