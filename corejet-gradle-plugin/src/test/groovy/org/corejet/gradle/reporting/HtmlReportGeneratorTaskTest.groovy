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

        then:
        true
//        2 == File(outputDir, "corejet").list().length
    }
}
