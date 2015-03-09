package org.corejet.gradle

import org.corejet.gradle.reporting.HtmlReportGeneratorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project

/**
 * Created by kwood on 09/03/15.
 */
public class CorejetPlugin implements Plugin<Project> {
    private Project project
    private CorejetExtension extension;

    @Override
    public void apply(Project project) {
        this.project = project

        extension = project.extensions.create(CorejetExtension.NAME, CorejetExtension, this.project)

        def htmlReportGeneratorTask = this.project.tasks.create ( HtmlReportGeneratorTask.NAME, HtmlReportGeneratorTask )
        htmlReportGeneratorTask.setProject(this.project)

        this.project.afterEvaluate {
            htmlReportGeneratorTask.setCorejetBaseDirectory(extension.baseDirectory)
        }
    }
}
