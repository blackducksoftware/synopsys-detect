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

import javax.annotation.PostConstruct

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

@Component
class GradleParsingPackager {
    private final Logger logger = LoggerFactory.getLogger(GradleParsingPackager.class)

    @Value('${packman.gradle.path}')
    String gradlePath

    @Value('${packman.gradle.excluded.configurations}')
    String excludedConfigurationNames

    @Value('${packman.gradle.included.configurations}')
    String includedConfigurationNames

    @Value('${packman.gradle.excluded.projects}')
    String excludedProjectNames

    @Value('${packman.gradle.included.projects}')
    String includedProjectNames

    @Autowired
    ExecutableFinder executableFinder

    @Autowired
    DependenciesParser dependenciesParser

    @Autowired
    PropertiesParser propertiesParser

    @Autowired
    ProjectsParser projectsParser

    private ExcludedIncludedFilter projectNamesFilter
    private ExcludedIncludedFilter configurationNamesFilter

    @PostConstruct
    void init() {
        projectNamesFilter = new ExcludedIncludedFilter(excludedProjectNames, includedProjectNames)
        configurationNamesFilter = new ExcludedIncludedFilter(excludedConfigurationNames, includedConfigurationNames)
    }

    DependencyNode extractRootProjectNode(String sourcePath) {
        if (!gradlePath) {
            logger.info('packman.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = executableFinder.findExecutable('gradlew', sourcePath)
        }

        if (!gradlePath) {
            logger.info('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableFinder.findExecutable('gradle')
        }

        //first, get the project metadata
        String properties = "${gradlePath} properties".execute(null, new File(sourcePath)).text
        DependencyNode rootProjectDependencyNode = propertiesParser.createProjectDependencyNodeFromProperties(properties)

        //next, get the root dependencies
        String dependencies = "${gradlePath} dependencies".execute(null, new File(sourcePath)).text
        dependenciesParser.populateDependencyNodeFromDependencies(rootProjectDependencyNode, dependencies, configurationNamesFilter)

        //now, if there are subprojects, collect those dependencies and add them to the rootProject
        //while this may seem like bananas, it is the best way to add subproject dependencies to the Hub
        GradleProjectName rootProjectName = new GradleProjectName()
        rootProjectName.name = rootProjectDependencyNode.name
        String projects = "${gradlePath} projects".execute(null, new File(sourcePath)).text
        projectsParser.populateWithSubProjects(rootProjectName, projects, projectNamesFilter)
        rootProjectName.children.each {
            addSubProjects(it, gradlePath, sourcePath, rootProjectDependencyNode, configurationNamesFilter)
        }

        rootProjectDependencyNode
    }

    void addSubProjects(GradleProjectName subProjectName, String gradlePath, String sourcePath, DependencyNode rootProjectDependencyNode, configurationNamesFilter) {
        String dependencies = "${gradlePath} ${subProjectName}:dependencies".execute(null, new File(sourcePath)).text
        dependenciesParser.populateDependencyNodeFromDependencies(rootProjectDependencyNode, dependencies, configurationNamesFilter)
        subProjectName.children.each {
            addSubProjects(it, gradlePath, sourcePath, rootProjectDependencyNode, configurationNamesFilter)
        }
    }
}
