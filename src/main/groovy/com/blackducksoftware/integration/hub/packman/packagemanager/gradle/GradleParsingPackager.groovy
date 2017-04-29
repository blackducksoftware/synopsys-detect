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

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.packman.Packager
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder

class GradleParsingPackager extends Packager {
    private final Logger logger = LoggerFactory.getLogger(GradleParsingPackager.class)

    static final String FIRST_COMPONENT_OF_CONFIGURATION = '+---'
    static final String COMPONENT_PREFIX = '--- '
    static final String SEEN_ELSEWHERE_SUFFIX = ' (*)'
    static final String WINNING_VERSION_INDICATOR = ' -> '

    private ExecutableFinder executableFinder
    private String gradlePath
    private String buildFilePath

    GradleParsingPackager(final ExecutableFinder executableFinder, String gradlePath, final String pathContainingBuildGradle) {
        this.executableFinder = executableFinder
        this.gradlePath = gradlePath
        this.buildFilePath = pathContainingBuildGradle
    }

    @Override
    List<DependencyNode> makeDependencyNodes() {
        if (!gradlePath) {
            logger.info('packman.gradle.path not set in config - first try to find the gradle wrapper')
            gradlePath = executableFinder.findExecutable('gradlew', buildFilePath)
        }

        if (!gradlePath) {
            logger.info('gradle wrapper not found - trying to find gradle on the PATH')
            gradlePath = executableFinder.findExecutable('gradle')
        }

        String properties = "${gradlePath} properties".execute(null, new File(buildFilePath)).text
        DependencyNode rootProjectDependencyNode = createProjectDependencyNodeFromProperties(properties)

        String dependencies = "${gradlePath} dependencies".execute(null, new File(buildFilePath)).text
        populateDependencyNodeFromDependencies(rootProjectDependencyNode, dependencies)

        [rootProjectDependencyNode]
    }

    void populateDependencyNodeFromDependencies(DependencyNode rootProject, String dependencies) {
        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(rootProject)
        boolean processingConfiguration = false
        String configurationName = null
        String previousLine = null
        def nodeStack = new Stack()
        nodeStack.push(rootProject)
        def previousNode = null
        int treeLevel = 0

        String[] lines = dependencies.split('\n')
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                processingConfiguration = false
                configurationName = null
                previousLine = null
                nodeStack = new Stack()
                nodeStack.push(rootProject)
                previousNode = null
                treeLevel = 0
                continue
            }
            if (!processingConfiguration && line.startsWith(FIRST_COMPONENT_OF_CONFIGURATION)) {
                processingConfiguration = true
                configurationName = previousLine.substring(0, previousLine.indexOf(' - ')).trim()
                logger.info("processing of configuration ${configurationName} started")
            }
            if (!processingConfiguration) {
                previousLine = line
                continue
            }

            DependencyNode lineNode = createDependencyNodeFromOutputLine(line)
            if (lineNode == null) {
                previousLine = line
                continue
            }

            int lineTreeLevel = StringUtils.countMatches(line, '    ')
            if (lineTreeLevel == treeLevel + 1) {
                nodeStack.push(previousNode)
            } else if (lineTreeLevel < treeLevel) {
                (treeLevel - lineTreeLevel).times { nodeStack.pop() }
            } else if (lineTreeLevel != treeLevel) {
                logger.error "The tree level (${treeLevel}) and this line (${line}) with count ${lineTreeLevel} can't be reconciled."
            }
            dependencyNodeBuilder.addChildNodeWithParents(lineNode, [nodeStack.peek()])
            previousNode = lineNode
            treeLevel = lineTreeLevel
            previousLine = line
        }
    }

    DependencyNode createDependencyNodeFromOutputLine(String outputLine) {
        if (StringUtils.isBlank(outputLine) || !outputLine.contains(COMPONENT_PREFIX)) {
            logger.warn 'No input was provided.'
            return null
        }

        String cleanedOutput = StringUtils.trimToEmpty(outputLine)
        if (cleanedOutput.split(':').length != 3) {
            logger.error "The line can not be reasonably split in to the neccessary parts: ${outputLine}"
            return null
        }

        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length())
        if (cleanedOutput.endsWith(SEEN_ELSEWHERE_SUFFIX)) {
            cleanedOutput = cleanedOutput[0..(-1 * (SEEN_ELSEWHERE_SUFFIX.length() + 1))]
        }

        def (group, artifact, version) = cleanedOutput.split(':')
        if (version.contains(WINNING_VERSION_INDICATOR)) {
            int winningVersionIndex = version.indexOf(WINNING_VERSION_INDICATOR) + WINNING_VERSION_INDICATOR.length()
            version = version[winningVersionIndex..-1]
        }

        new DependencyNode(artifact, version, new MavenExternalId(group, artifact, version))
    }

    DependencyNode createProjectDependencyNodeFromProperties(String properties) {
        String group
        String name
        String version
        boolean processingProperties = false
        properties.split('\n').each { line ->
            if (line.startsWith(':properties')) {
                processingProperties = true
            }
            if (processingProperties) {
                if (line.startsWith('group: ') && !(group)) {
                    group = line[7..-1]
                } else if (line.startsWith('name: ') && !(name)) {
                    name = line[6..-1]
                } else if (line.startsWith('version: ') && !(version)) {
                    version = line[9..-1]
                }
            }
        }

        if (group && name && version) {
            return new DependencyNode(name, version, new MavenExternalId(group, name, version))
        } else {
            return null
        }
    }
}