/**
 * 
 */
package org.corejet.maven.reporting;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import org.apache.axis.utils.ByteArray;
import org.apache.axis.utils.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.DirectoryScanner;
import org.corejet.Configuration;
import org.corejet.model.Epic;
import org.corejet.model.RequirementsCatalogue;
import org.corejet.model.Scenario;
import org.corejet.model.ScenarioStatus;
import org.corejet.model.Story;
import org.corejet.model.exception.MergeException;
import org.corejet.model.exception.ParsingException;

/**
 * Gather together a set of report files to create a trended HTML report
 * @author rpickard
 *
 */
public class HistoricalTrendingReportMojo extends AbstractMojo {

	private static final String HISTORICAL_TRENDING_REPORT_START_XML = "historicalTrendingReportStart.xml";

	private static String HTML_END = "</body><html>";

	/** @parameter default-value="${project}" */
	private MavenProject mavenProject;

	private static final String REPORT_DESTINATION = "corejet-trend-report.html";

	/**
	 * @parameter expression="${dataPattern}" default-value="\*\*\/corejet-report.xml"
	 */
	private String dataPattern;

	/**
	 * @parameter expression="${dataBaseDir}" default-value="${project.build.directory}/../builds"
	 */
	private String dataBaseDir;

	/**  
	 * @{inheritDoc}
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[]{dataPattern});
		scanner.setBasedir(dataBaseDir);
		scanner.setCaseSensitive(false);
		scanner.scan();

		List<RequirementsCatalogue> catalogues = new ArrayList<RequirementsCatalogue>();
		for (String fileName : scanner.getIncludedFiles()){
			File file = new File(dataBaseDir+fileName);
			try {
				catalogues.add(new RequirementsCatalogue(new FileInputStream(file)));
			} catch (Exception e) {
				throw new MojoFailureException("Failed to parse input files",e);
			} 
		}

		// Create a master Catalogue
		RequirementsCatalogue masterCatalogue = catalogues.get(catalogues.size()-1);
		for(RequirementsCatalogue catalogue : catalogues){
			try {
				masterCatalogue = RequirementsCatalogue.merge(masterCatalogue, catalogue);
			} catch (MergeException e) {
				throw new MojoFailureException("Failed to generate report - error merging catalogues",e);
			}
		}

		try {
			
			
			File outputFile = new File(getBaseDirectory()+REPORT_DESTINATION);
			outputFile.createNewFile();
			PrintWriter output = new PrintWriter(outputFile);

			File startFile = new File(ClassLoader.getSystemResource(HISTORICAL_TRENDING_REPORT_START_XML).toURI());
			byte[] buffer = new byte[(int) startFile.length()];
			BufferedInputStream f = new BufferedInputStream(new FileInputStream(startFile));
			f.read(buffer);
			output.append(new String(buffer));
			output.append("<table id=\"trend\" class=\"treeTable\">");
			output.append("<thead><tr>");
			output.append("<th>");
			output.append("Id");
			output.append("</th>");
			for (RequirementsCatalogue catalogue : catalogues){
				output.append("<th>");
				output.append(catalogue.getExtractTime().toString());
				output.append("</th>");
			}
			output.append("</tr></thead>");
			output.append("<tbody>");
			
			int epicNumber=0;
			int storyNumber=0;
			int scenarioNumber=0;
			
			for (Epic epic : masterCatalogue.getEpics()){
				epicNumber++;
				output.append("<tr class=\"epic\" id=\"node-epic-"+epicNumber+"\">");
				output.append("<td>");
				output.append(epic.getTitle());
				output.append("</td>");
				output.append("</tr>");
				for (Story story : epic.getStories()){
					storyNumber++;
					output.append("<tr id=\"node-story-"+storyNumber+"\" class=\"story child-of-node-epic-"+epicNumber+"\">");
					output.append("<td>");
					output.append(story.getTitle());
					output.append("</td>");
					output.append("</tr>");
					for (Scenario scenario : story.getScenarios()){
						scenarioNumber++;
						output.append("<tr id=\"node-scenario-"+scenarioNumber+"\" class=\"scenario child-of-node-story-"+storyNumber+"\">");
						output.append("<td>");
						output.append(scenario.getName());
						output.append("</td>");
						for (RequirementsCatalogue catalogue : catalogues){
							ScenarioStatus result = null;
							// find the right result
							for (Epic e : catalogue.getEpics()){
								if (e.getId().equals(epic.getId())){
									for (Story s : e.getStories()){
										if(s.getId().equals(story.getId())){
											for (Scenario scen : s.getScenarios()){
												if (scen.getName().equals(scenario.getName())){
													result = scen.getStatus();
													break;
												}
											}
											break;
										}
									}
									break;
								}
							}
							if (null!=result){
								switch (result) {
								case PASS:
									output.append("<td style=\"background-color:green\">");
									break;

								case FAIL:
									output.append("<td style=\"background-color:red\">");
									break;

								case DEFECT:
									output.append("<td style=\"background-color:purple\">");
									break;

								case MISMATCH:
									output.append("<td style=\"background-color:orange\">");
									break;

								case EMPTY:
									output.append("<td style=\"background-color:#94CA94;\">");
									break;

								case TODO:
									output.append("<td style=\"background-color:grey\">");
									break;

								case NA:
									output.append("<td style=\"background-color:white\">");
									break;				

								case PENDING:
									output.append("<td style=\"background-color:lightblue\">");
									break;	

								default:
									output.append("<td>");
									break;
								}
								output.append(result.toString());
							} else {
								output.append("<td style=\"background-color:grey\">");
							}
							output.append("</td>");
						}
						output.append("</tr>");
					}
				}
			}
			output.append("</tbody>");
			output.append("</table>");
			output.append(HTML_END);
			output.close();
		} catch (Exception e) {
			throw new MojoFailureException("Failed to generate report",e);
		}		

	}


	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public void setMavenProject(MavenProject mavenProject) {
		this.mavenProject = mavenProject;
	}

	public String getDataPattern() {
		return dataPattern;
	}

	public void setDataPattern(String dataPattern) {
		this.dataPattern = dataPattern;
	}

	public String getDataBaseDir() {
		return dataBaseDir;
	}

	public void setDataBaseDir(String dataBaseDir) {
		this.dataBaseDir = dataBaseDir;
	}
	
	private String getBaseDirectory(){
		try {
			return Configuration.getProperty("corejet.report.directory");
		} catch (RuntimeException e) {
			return "target/corejet";
		}
	}

}
