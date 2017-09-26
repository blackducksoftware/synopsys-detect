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
package com.blackducksoftware.integration.hub.detect.bomtool.maven

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.util.ExcludedIncludedFilter

import groovy.transform.TypeChecked

@Component
@TypeChecked
class MavenCodeLocationPackager {
    Logger logger = LoggerFactory.getLogger(MavenCodeLocationPackager.class)

    public static final List<String> indentationStrings = ['+- ', '|  ', '\\- ', '   ']

    private List<DetectCodeLocation> codeLocations = []
    private DetectCodeLocation currentCodeLocation = null
    private Stack<DependencyNode> dependencyParentStack = new Stack<>()
    private boolean parsingProjectSection
    private int level

    @Autowired
    ExternalIdFactory externalIdFactory

    public List<DetectCodeLocation> extractCodeLocations(String sourcePath, String mavenOutputText, String excludedModules, String includedModules) {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter(excludedModules, includedModules)
        codeLocations = []
        currentCodeLocation = null
        dependencyParentStack = new Stack<>()
        parsingProjectSection = false
        level = 0
        for (String line : mavenOutputText.split(System.lineSeparator())) {
            if (!(line ==~ /\[.*INFO.*\].*/) || line ==~ /\[.*INFO.*\].*Downloaded:.*/ || line ==~ /\[.*INFO.*\].*Downloading:.*/) {
                // If the line does not start with [INFO] and have content, we will ignore it
                // We also ignore lines for downloads
                continue
            }

            String[] groups = (line =~ /.*INFO.*? (.*)/)[0] as String[]
            line = groups[1]
            if (!line.trim()) {
                continue
            }
            if (line ==~ /.*---.*maven-dependency-plugin.*/) {
                parsingProjectSection = true
                continue
            }

            if (!parsingProjectSection) {
                continue
            }

            if (parsingProjectSection && currentCodeLocation == null) {
                //this is the first line of a new code location, the following lines will be the tree of dependencies for this code location
                DetectCodeLocation detectCodeLocation = createNewCodeLocation(sourcePath, line)
                if (filter.shouldInclude(detectCodeLocation.getBomToolProjectName())) {
                    this.currentCodeLocation = detectCodeLocation
                    codeLocations.add(detectCodeLocation)
                } else {
                    currentCodeLocation = null
                    dependencyParentStack.clear()
                    parsingProjectSection = false
                    level = 0
                }
                continue
            }

            final boolean finished = line.contains('--------')
            if (finished) {
                currentCodeLocation = null
                dependencyParentStack.clear()
                parsingProjectSection = false
                level = 0
                continue
            }

            int previousLevel = level
            String cleanedLine = calculateCurrentLevelAndCleanLine(line)
            DependencyNode dependencyNode = textToDependencyNode(cleanedLine)
            if (!dependencyNode) {
                continue
            }

            if (currentCodeLocation != null) {
                if (level == 1) {
                    //a direct dependency, clear the stack and add this as a potential parent for the next line
                    currentCodeLocation.dependencies.add(dependencyNode)
                    dependencyParentStack.clear()
                    dependencyParentStack.push(dependencyNode)
                } else {
                    //level should be greater than 1
                    if (level == previousLevel) {
                        //a sibling of the previous dependency
                        dependencyParentStack.pop()
                        dependencyParentStack.peek().children.add(dependencyNode)
                        dependencyParentStack.push(dependencyNode)
                    } else if (level > previousLevel) {
                        //a child of the previous dependency
                        dependencyParentStack.peek().children.add(dependencyNode)
                        dependencyParentStack.push(dependencyNode)
                    } else {
                        //a child of a dependency further back than 1 line
                        previousLevel.downto(level) { dependencyParentStack.pop() }
                        dependencyParentStack.peek().children.add(dependencyNode)
                        dependencyParentStack.push(dependencyNode)
                    }
                }
            }
        }

        codeLocations
    }

    DetectCodeLocation createNewCodeLocation(String sourcePath, String line) {
        DependencyNode dependencyNode = textToDependencyNode(line)
        String codeLocationSourcePath = sourcePath
        if (!sourcePath.endsWith(dependencyNode.name)) {
            codeLocationSourcePath += '/' + dependencyNode.name
        }
        new DetectCodeLocation(BomToolType.MAVEN, codeLocationSourcePath, dependencyNode.name, dependencyNode.version, dependencyNode.externalId, new HashSet<>())
    }

    String calculateCurrentLevelAndCleanLine(String line) {
        level = 0
        String cleanedLine = line
        for (String pattern : indentationStrings) {
            while (cleanedLine.contains(pattern)) {
                level++
                cleanedLine = cleanedLine.replaceFirst(Pattern.quote(pattern), '')
            }
        }

        return cleanedLine
    }

    DependencyNode textToDependencyNode(final String componentText) {
        Matcher gavMatcher = componentText =~ /(.*?):(.*?):(.*?):([^:]*)(:(.*))*/
        if (!gavMatcher.matches()) {
            logger.debug("${componentText} does not match pattern ${gavMatcher.pattern().toString()}")
            return null
        }

        String group = gavMatcher.group(1)
        String artifact = gavMatcher.group(2)
        String version = gavMatcher.group(4)

        ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version)
        def node = new DependencyNode(artifact, version, externalId)
        return node
    }
}
