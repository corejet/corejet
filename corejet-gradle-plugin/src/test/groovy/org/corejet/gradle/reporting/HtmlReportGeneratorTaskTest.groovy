package org.corejet.gradle.reporting

import org.codehaus.plexus.util.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by kwood on 10/03/15.
 */
class HtmlReportGeneratorTaskTest extends Specification {
    def "it generates html reports"() {
        String anExpectedLine = "<p>Given, there is a user joebloggs with password password</p><p>When, I try to log in " +
                "with username joebloggs and password\t\t\t\t\tpassword</p><p>Then, the system grants me access</p><p>" +
                "Then, I am taken to a home page</p></td><td></td><td>pass</td><td>100%</td>"
        given:
        Project project = ProjectBuilder.builder().build()
        project.configurations {}

        def htmlReportGeneratorTask = project.tasks.create ( HtmlReportGeneratorTask.NAME, HtmlReportGeneratorTask )
        htmlReportGeneratorTask.setProject(project)
        htmlReportGeneratorTask.setCorejetBaseDirectory("build/corejet")

        for (File input : new File("src/test/resources/corejet/test-output/").listFiles()){
            if (input.isFile()){
                FileUtils.copyFileToDirectory(input, new File("build/corejet/test-output/"));
            }
        }

        when:
        htmlReportGeneratorTask.generateHtmlReport()

        File corejetReportFile = new File("build/corejet/corejet-report.html");

        then:
        corejetReportFile.isFile()
        corejetReportFile.size() > 18000;
        containsAnExpectedLine(corejetReportFile, anExpectedLine);
    }

    def containsAnExpectedLine(corejetReportFile, anExpectedLine) {
        boolean containsLine = false;
        corejetReportFile.eachLine { line ->
            if(line.contains(anExpectedLine)) {
                containsLine = true;
            }
        }
        return containsLine
    }
}
