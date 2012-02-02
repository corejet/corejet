
package org.corejet.maven.reporting;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.corejet.model.RequirementsCatalogue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.PatternFilenameFilter;

/**
 * Generate an HTML report using an xsl Template
 * @goal generate-report
 * 
 * @author rpickard
 * 
 */
public class HtmlReportGeneratorMojo extends AbstractMojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlReportGeneratorMojo.class);

	private static final String COREJET_TO_HTML_XSL = "/corejet-to-html.xsl";


	private static final String VISUALIZATION_LIBRARY_ARTIFACT_ID = "corejet-visualization";


	/** @parameter default-value="${project}" */
	private MavenProject mavenProject;

	/** @parameter expression="${corejet.report.directory}" default-value="target/corejet" */
	private String corejetReportDirectory;


	private static final String TEST_OUTPUT_DIRECTORY = "/test-output";
	private static final String REPORT_DESTINATION = "/corejet-report.html";
	private static final String TEST_OUTPUT_DESTINATION = "/corejet-report.xml";

	/**  
	 * @{inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {

		try {

			// Merge the reports
			FilenameFilter filter = new PatternFilenameFilter("corejet-.*");
			File[] reports = new File(corejetReportDirectory+TEST_OUTPUT_DIRECTORY).listFiles(filter);

			RequirementsCatalogue result = null;
			LOGGER.info("Found "+reports.length+" XML report files");
			for(int i = 0 ; i<reports.length ; i++){
				LOGGER.info("Merging report "+reports[i].getName());
				result = RequirementsCatalogue.merge(new RequirementsCatalogue(new FileInputStream(reports[i])), result);
			}

			File testOutputFile = new File(corejetReportDirectory+TEST_OUTPUT_DIRECTORY+TEST_OUTPUT_DESTINATION);
			result.write(new FileOutputStream(testOutputFile));

			File outputDir = new File(corejetReportDirectory+TEST_OUTPUT_DIRECTORY);
			outputDir.mkdir();

			File report = new File(corejetReportDirectory+REPORT_DESTINATION);
			report.createNewFile();

			TransformerFactory tFactory = TransformerFactory.newInstance();

			Transformer transformer = tFactory.newTransformer(new StreamSource(this.getClass().getResourceAsStream(COREJET_TO_HTML_XSL)));

			//read the testOutput with BufferedReader
			BufferedReader reader	= new BufferedReader(new FileReader(testOutputFile));

			String testOutputString = "";
			String line;
			while ((line = reader.readLine()) != null) {
				testOutputString += line.replace("'", "") ;
			} 

			transformer.transform(new StreamSource(new ByteArrayInputStream(testOutputString.getBytes())),new StreamResult( new FileOutputStream(report)));

			// TODO Hacky fix for line breaks in json
			Map<String, String> replacements = new HashMap<String, String>();
			replacements.put("p>\r\n","p>");
			replacements.put("div>\r\n","div>");
			replacements.put("strong>\r\n","strong>");
			replacements.put("br>\r\n","br>");
			replacements.put("h2>\r\n","h2>");
			replaceAll(report, replacements);
		}
		catch (Exception e) {
			throw new MojoFailureException("Failed to generate CoreJet HTML report in directory "+corejetReportDirectory,e);
		}

		// could not instantiate maven project in tests ,skip if null
		if (null!=mavenProject){
			try {
				// Find the visualization dependency
				File vizualization = null;
				@SuppressWarnings("rawtypes")
				Set artifacts = mavenProject.getDependencyArtifacts();
				for (@SuppressWarnings("rawtypes")
				Iterator artifactIterator = artifacts.iterator(); artifactIterator.hasNext();) {
					Artifact artifact = (Artifact) artifactIterator.next();
					if (artifact.getArtifactId().equals(VISUALIZATION_LIBRARY_ARTIFACT_ID)) {
						vizualization = artifact.getFile();
					} 
				}

				// getting the contents of the vizualization dependency
				JarFile jarFile = new JarFile(vizualization);
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry jarEntry = (JarEntry) entries.nextElement();
					// Get the jar entry as a file
					File reportFile = new File(corejetReportDirectory + File.separator + jarEntry.getName());
					
					// if we are looking at a directory, create it and continue
					if (reportFile.isDirectory()) {
						reportFile.mkdir();
						continue;
					}
					
					// copy the files
					InputStream reportInput = jarFile.getInputStream(jarEntry);
					FileOutputStream reportOutput = new FileOutputStream(reportFile);
					while (reportInput.available() > 0) {
						reportOutput.write(reportInput.read());
					}
					reportOutput.close();
					reportInput.close();
				}
			} catch (Exception e) {
				throw new MojoFailureException("Failed to extract visualization resources",e);
			}
		}

	}


	public void setMavenProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}


	private void replaceAll(File file, Map<String,String> replacements) throws MojoFailureException{

		try {
			File out = new File(corejetReportDirectory+"/temp-file.html");
			out.deleteOnExit();

			BufferedReader reader = new BufferedReader(new FileReader(file));
			PrintWriter writer = new PrintWriter(new FileWriter(out));
			String line = null;
			while ((line = reader.readLine()) != null) {
				line += "\r\n";
				for (Entry<String, String> replacement : replacements.entrySet()){
					line = line.replaceAll(replacement.getKey(), replacement.getValue());
				}
				writer.print(line);
			}
			reader.close();
			writer.close();

			FileUtils.copyFile(out, file);

		} catch (IOException e) {
			throw new MojoFailureException("Failed to make replacements in report",e);
		}
	}


	public String getCorejetBaseDirectory() {
		return corejetReportDirectory;
	}


	public void setCorejetBaseDirectory(String corejetBaseDirectory) {
		this.corejetReportDirectory = corejetBaseDirectory;
	}


}
