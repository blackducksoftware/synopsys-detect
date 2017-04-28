/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.Packager
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder
import com.google.gson.Gson

class GradleFilePackager extends Packager {
    private final Logger logger = LoggerFactory.getLogger(GradleFilePackager.class)

    private Gson gson
    private ExecutableFinder executableFinder
    private String gradlePath
    private String buildFilePath

    GradleFilePackager(final Gson gson, final ExecutableFinder executableFinder, String gradlePath, final String pathContainingBuildGradle) {
        this.gson = gson
        this.executableFinder = executableFinder
        this.gradlePath = gradlePath
        this.buildFilePath = pathContainingBuildGradle
    }

    @Override
    List<DependencyNode> makeDependencyNodes() {
        if (!gradlePath) {
            logger.info('packman.gradle.path not set in config - trying to find gradle on the PATH')
            gradlePath = executableFinder.findExecutable('gradle')
        }

        if (!gradlePath) {
            logger.info('Could not find gradle - trying a gradle wrapper')
            gradlePath = 'gradlew'
        }

        File initScriptFile = File.createTempFile('init-_packman', '.gradle')
        initScriptFile.deleteOnExit()
        String initScriptContents = getClass().getResourceAsStream('/init-packman-gradle').getText(StandardCharsets.UTF_8.name())
        initScriptFile << initScriptContents
        String initScriptPath = initScriptFile.absolutePath
        logger.info("using ${initScriptPath} as the path for the gradle init script")
        String output = "${gradlePath} build -x test --init-script=${initScriptPath}".execute(null, new File(buildFilePath)).text
        logger.info(output)

        File dependencyNodeFile = new File(buildFilePath, 'build')
        dependencyNodeFile = new File(dependencyNodeFile, 'blackduck')
        dependencyNodeFile = new File(dependencyNodeFile, 'dependencyNodes.json')
        String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        DependencyNode rootProjectDependencyNode = gson.fromJson(dependencyNodeJson, DependencyNode.class);

        [rootProjectDependencyNode]
    }
}
