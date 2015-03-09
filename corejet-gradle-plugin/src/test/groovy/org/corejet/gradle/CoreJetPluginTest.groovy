package org.corejet.gradle

import org.corejet.gradle.reporting.HtmlReportGeneratorTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

/**
 * Created by kwood on 10/03/15.
 */
class CorejetPluginTest extends Specification {
    public void test() {
        given:
        Project project = ProjectBuilder.builder().build();

        when:
        project.pluginManager.apply 'org.corejet.gradle-plugin'

        then:
        project.tasks["$HtmlReportGeneratorTask.NAME"] instanceof HtmlReportGeneratorTask
    }
}
