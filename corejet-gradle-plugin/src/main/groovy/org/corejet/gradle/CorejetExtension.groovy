package org.corejet.gradle

import org.gradle.api.Project

/**
 * Created by kwood on 13/03/15.
 */
class CorejetExtension {
    final static String NAME = 'corejet'

    GString baseDirectory;

    CorejetExtension(Project project) {
        baseDirectory = "${project.buildDir}/corejet"
    }
}
