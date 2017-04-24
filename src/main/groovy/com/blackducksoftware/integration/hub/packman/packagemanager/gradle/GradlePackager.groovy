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
import org.springframework.beans.factory.annotation.Value

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.packman.Packager
import com.blackducksoftware.integration.hub.packman.packagemanager.ExecutableFinder

class GradlePackager extends Packager {
    private final Logger logger = LoggerFactory.getLogger(GradlePackager.class)

    static final String COMPONENT_PREFIX = '--- '
    static final String SEEN_ELSEWHERE_SUFFIX = ' (*)'
    static final String WINNING_VERSION_INDICATOR = ' -> '

    @Value('${packman.gradle.path}')
    String gradlePath

    ExecutableFinder executableFinder
    String buildFilePath

    GradlePackager(final ExecutableFinder executableFinder, final String pathContainingBuildGradle) {
        this.executableFinder = executableFinder
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

        
                String output = "${gradlePath} dependencies".execute(null, buildFilePath).text
        String[] lines = output.split('\n')

        def projects = [
            new DependencyNode('project', 'version', new MavenExternalId('group', 'project', 'version'))
        ]
        def children = createDependencyNodesFromOutputLines(projects[0], output.split('\n'))

        projects
    }

    List<DependencyNode> createDependencyNodesFromOutputLines(DependencyNode rootProject, List<String> lines) {
        DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(rootProject)
        def nodeStack = new Stack()
        nodeStack.push(rootProject)
        def previousNode = null
        int treeLevel = 0

        for (String line : lines) {
            DependencyNode lineNode = createDependencyNodeFromOutputLine(line)
            if (lineNode == null) {
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

        new DependencyNode(artifact, version, new MavenExternalId(Forge.maven, group, artifact, version))
    }
}
