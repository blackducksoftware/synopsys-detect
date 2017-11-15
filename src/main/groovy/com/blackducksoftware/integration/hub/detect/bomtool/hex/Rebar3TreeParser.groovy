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
package com.blackducksoftware.integration.hub.detect.bomtool.hex

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.model.DetectProject

import groovy.transform.TypeChecked

@Component
@TypeChecked
class Rebar3TreeParser {
    private final Logger logger = LoggerFactory.getLogger(Rebar3TreeParser.class)

    public static final String LAST_DEPENDENCY_CHARACTER = '└'
    public static final String NTH_DEPENDENCY_CHARACTER = '├'
    public static final String HORIZONTAL_SEPARATOR_CHARACTER = '─'
    public static final String INNER_LEVEL_CHARACTER = '│'
    public static final String INNER_LEVEL_PREFIX = INNER_LEVEL_CHARACTER + ' '.multiply(2)
    public static final String OUTER_LEVEL_PREFIX = ' '.multiply(3)
    public static final String PROJECT_FORGE_STRING = '(project app)'

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExternalIdFactory externalIdFactory

    public DetectCodeLocation parseRebarTreeOutput(List<String> dependencyTreeOutput, DetectProject detectProject, String sourcePath) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph()
        String projectName = ''
        String projectVersionName = ''
        Stack<Dependency> dependencyStack = new Stack<Dependency>()
        int previousTreeLevel = 0
        Dependency previousDependency

        try {
            for (String line: dependencyTreeOutput) {
                if (line.contains(HORIZONTAL_SEPARATOR_CHARACTER)) {
                    Dependency currentDependency = createDependencyFromLine(line)

                    final int currentTreeLevel = getDependencyLevelFromLine(line)

                    if (currentTreeLevel == previousTreeLevel + 1) {
                        dependencyStack.push(previousDependency)
                    } else if (currentTreeLevel < previousTreeLevel) {
                        int levelDelta = (previousTreeLevel - currentTreeLevel)
                        for (int levels = 0; levels < levelDelta; levels++) {
                            dependencyStack.pop()
                        }
                    } else if (currentTreeLevel != previousTreeLevel) {
                        logger.error(String.format('The tree level (%s) and this line (%s) with count %s can\'t be reconciled.', previousTreeLevel, line, currentTreeLevel))
                    }

                    if (dependencyStack.size() == 0) {
                        if ( isProject(line) ) {
                            projectName = currentDependency.name
                            projectVersionName = currentDependency.version
                        } else {
                            graph.addChildToRoot(currentDependency)
                        }
                    } else if (dependencyStack.size() == 1 && dependencyStack.peek().name.equals(projectName) && dependencyStack.peek().version.equals(projectVersionName)) {
                        graph.addChildToRoot(currentDependency)
                    } else {
                        graph.addChildWithParents(currentDependency, dependencyStack.peek())
                    }

                    previousDependency = currentDependency
                    previousTreeLevel = currentTreeLevel
                }
            }
        } catch (final Exception e) {
            logger.error('Exception parsing rebar output: ' + e.getMessage())
        }
        detectProject.setProjectNameIfNotSet(projectName)
        detectProject.setProjectVersionNameIfNotSet(projectVersionName)

        final ExternalId id = externalIdFactory.createNameVersionExternalId(Forge.HEX, projectName, projectVersionName)

        return new DetectCodeLocation(BomToolType.HEX, sourcePath, projectName, projectVersionName, id, graph)
    }

    private Dependency createDependencyFromLine(String line) {
        String nameVersionLine = reduceLineToNameVersion(line)
        String name = nameVersionLine.substring(0, nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER))
        String version = nameVersionLine.substring(nameVersionLine.lastIndexOf(HORIZONTAL_SEPARATOR_CHARACTER) + 1)
        ExternalId externalId  = externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version)

        return new Dependency(name, version, externalId)
    }

    private String reduceLineToNameVersion(String line) {
        for (String specialCharacter : [
            LAST_DEPENDENCY_CHARACTER,
            NTH_DEPENDENCY_CHARACTER,
            INNER_LEVEL_CHARACTER
        ]) {
            line = line.replaceAll(specialCharacter, '')
        }
        line = line.replaceFirst(HORIZONTAL_SEPARATOR_CHARACTER, '')
        if (line.endsWith(')')) {
            line = line.substring(0, line.lastIndexOf('('))
        }

        return line.trim()
    }

    private int getDependencyLevelFromLine(String line) {
        int level = 0
        while(line.startsWith(INNER_LEVEL_PREFIX) || line.startsWith(OUTER_LEVEL_PREFIX)) {
            line = line.substring(3)
            level++
        }

        return level
    }

    private boolean isProject(String line) {
        String forgeString = ''
        if (line.endsWith(')')) {
            forgeString = line.substring(line.lastIndexOf('('))
        }

        return PROJECT_FORGE_STRING.equals(forgeString)
    }
}
