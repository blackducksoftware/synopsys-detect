/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.bomtool.gradle

import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject

import groovy.transform.TypeChecked

@TypeChecked
@Component
class GradleDependenciesParser {
    private final Logger logger = LoggerFactory.getLogger(GradleDependenciesParser.class)

    static final String DEPENDENCY_INDICATOR = '+---'
    static final String LAST_CHILD_INDICATOR = '\\---'
    static final String COMPONENT_PREFIX = '--- '
    static final String SEEN_ELSEWHERE_SUFFIX = ' (*)'
    static final String WINNING_INDICATOR = ' -> '

    String rootProjectSourcePath = ""
    String rootProjectGroup  = ""
    String rootProjectName  = ""
    String rootProjectVersionName  = ""

    String projectSourcePath = ""
    String projectGroup = ""
    String projectName = ""
    String projectVersionName = ""

    public ExternalIdFactory externalIdFactory;

    public GradleDependenciesParser(ExternalIdFactory externalIdFactory){
        this.externalIdFactory = externalIdFactory;
    }

    DetectCodeLocation parseDependencies(DetectProject detectProject, InputStream dependenciesInputStream) {
        //DependencyNode tempRoot = new DependencyNode("project", "version", externalIdFactory.createMavenExternalId("group", "project", "version"))

        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        //DependencyNodeBuilder dependencyNodeBuilder = new DependencyNodeBuilder(tempRoot)
        boolean processingMetaData = false
        boolean processingConfiguration = false
        String configurationName = null
        String previousLine = null
        Stack<Dependency> nodeStack = new Stack<>()
        Dependency previousNode = null
        int treeLevel = 0

        dependenciesInputStream.eachLine("UTF-8") { line ->
            /**
             * The meta data section will be at the end of the file after all of the 'gradle dependencies' output
             */
            if (line.startsWith('DETECT META DATA START')) {
                processingMetaData = true
                return
            }
            if (line.startsWith('DETECT META DATA END')) {
                processingMetaData = false
                return
            }
            if (processingMetaData) {
                processMetaDataLine(line)
                return
            }

            if (StringUtils.isBlank(line)) {
                processingConfiguration = false
                configurationName = null
                previousLine = null
                nodeStack = new Stack()
                previousNode = null
                treeLevel = 0
                return
            }
            if (!processingConfiguration && line.startsWith(DEPENDENCY_INDICATOR)) {
                processingConfiguration = true
                configurationName = previousLine
                if (previousLine.contains(' -  ')) {
                    configurationName = previousLine.substring(0, previousLine.indexOf(' - ')).trim()
                } else {
                    configurationName = previousLine.trim()
                }
                logger.info("processing of configuration ${configurationName} started")
            }
            if (!processingConfiguration) {
                previousLine = line
                return
            }

            Dependency lineNode = createDependencyNodeFromOutputLine(line)
            if (lineNode == null) {
                previousLine = line
                return
            }

            int lineTreeLevel = getLineLevel(line)
            if (lineTreeLevel == treeLevel + 1) {
                nodeStack.push(previousNode)
            } else if (lineTreeLevel < treeLevel) {
                (treeLevel - lineTreeLevel).times { nodeStack.pop() }
            }  else if (lineTreeLevel != treeLevel) {
                logger.error "The tree level (${treeLevel}) and this line (${line}) with count ${lineTreeLevel} can't be reconciled."
            }
            if (nodeStack.size() == 0){
                graph.addChildToRoot(lineNode);
            }else{
                graph.addChildWithParents(lineNode, [nodeStack.peek()])
            }
            previousNode = lineNode
            treeLevel = lineTreeLevel
            previousLine = line
        }

        detectProject.setProjectNameIfNotSet(rootProjectName)
        detectProject.setProjectVersionNameIfNotSet(rootProjectVersionName)

        ExternalId id = externalIdFactory.createMavenExternalId(projectGroup, projectName, projectVersionName);
        new DetectCodeLocation(BomToolType.GRADLE, projectSourcePath, projectName, projectVersionName,
                id, graph)
    }

    public int getLineLevel(String line) {
        if (line.startsWith(DEPENDENCY_INDICATOR) || line.startsWith(LAST_CHILD_INDICATOR)) {
            return 0
        }
        String modifiedLine = ""
        int indexToCut = line.length()
        if (line.contains(DEPENDENCY_INDICATOR)) {
            indexToCut =line.indexOf(DEPENDENCY_INDICATOR)
        } else if (line.contains(LAST_CHILD_INDICATOR)) {
            indexToCut =line.indexOf(LAST_CHILD_INDICATOR)
        }
        modifiedLine = line.substring(0, indexToCut)
        if (!modifiedLine.startsWith('|')) {
            modifiedLine = '|' + modifiedLine
        }
        modifiedLine = modifiedLine.replace('     ', '    |')
        modifiedLine = modifiedLine.replace('||', '|')
        if (modifiedLine.endsWith('|')) {
            modifiedLine = modifiedLine.substring(0, modifiedLine.length() - 5)
        }
        int matches = StringUtils.countMatches(modifiedLine, '|')

        return matches
    }

    Dependency createDependencyNodeFromOutputLine(String outputLine) {
        if (StringUtils.isBlank(outputLine) || !outputLine.contains(COMPONENT_PREFIX)) {
            return null
        }

        String cleanedOutput = StringUtils.trimToEmpty(outputLine)
        cleanedOutput = cleanedOutput.substring(cleanedOutput.indexOf(COMPONENT_PREFIX) + COMPONENT_PREFIX.length())
        if (cleanedOutput.endsWith(SEEN_ELSEWHERE_SUFFIX)) {
            cleanedOutput = cleanedOutput[0..(-1 * (SEEN_ELSEWHERE_SUFFIX.length() + 1))]
        }

        String[] gav = cleanedOutput.split(':')
        if (cleanedOutput.contains(WINNING_INDICATOR)) {
            // WINNING_INDICATOR can point to an entire GAV not just a version
            String winningSection = cleanedOutput.substring(cleanedOutput.indexOf(WINNING_INDICATOR) + WINNING_INDICATOR.length())
            if (winningSection.contains(':')) {
                gav = winningSection.split(':')
            } else {
                gav[2] = winningSection
            }
        }
        if (gav.length != 3) {
            logger.error "The line can not be reasonably split in to the neccessary parts: ${outputLine}"
            return null
        }
        String group = gav[0]
        String artifact = gav[1]
        String version = gav[2]

        new Dependency(artifact, version, externalIdFactory.createMavenExternalId(group, artifact, version))
    }

    private void processMetaDataLine(String metaDataLine) {
        if (metaDataLine.startsWith('rootProjectPath:')) {
            rootProjectSourcePath = metaDataLine.substring('rootProjectPath:'.length()).trim()
        } else if (metaDataLine.startsWith('rootProjectGroup:')) {
            rootProjectGroup = metaDataLine.substring('rootProjectGroup:'.length()).trim()
        } else if (metaDataLine.startsWith('rootProjectName:')) {
            rootProjectName = metaDataLine.substring('rootProjectName:'.length()).trim()
        } else if (metaDataLine.startsWith('rootProjectVersion:')) {
            rootProjectVersionName = metaDataLine.substring('rootProjectVersion:'.length()).trim()
        } else if (metaDataLine.startsWith('projectPath:')) {
            projectSourcePath = metaDataLine.substring('projectPath:'.length()).trim()
        } else if (metaDataLine.startsWith('projectGroup:')) {
            projectGroup = metaDataLine.substring('projectGroup:'.length()).trim()
        } else if (metaDataLine.startsWith('projectName:')) {
            projectName = metaDataLine.substring('projectName:'.length()).trim()
        } else if (metaDataLine.startsWith('projectVersion:')) {
            projectVersionName = metaDataLine.substring('projectVersion:'.length()).trim()
        }
    }
}
