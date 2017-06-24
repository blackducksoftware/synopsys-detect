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
package com.blackducksoftware.integration.hub.detect.bomtool.rubygems

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

class GemlockNodeParser {
    private final Logger logger = LoggerFactory.getLogger(GemlockNodeParser.class)

    private NameVersionNode rootNameVersionNode
    private NameVersionNodeBuilder nameVersionNodeBuilder
    private HashSet<String> directDependencyNames
    private NameVersionNode currentParent

    private boolean inSpecsSection = false
    private boolean inDependenciesSection = false

    void parseProjectDependencies(NameVersionNodeTransformer nameVersionNodeTransformer, DependencyNode rootProject, final String gemfileLockContents) {
        rootNameVersionNode = new NameVersionNodeImpl([name: rootProject.name, version: rootProject.version])
        nameVersionNodeBuilder = new NameVersionNodeBuilder(rootNameVersionNode)
        directDependencyNames = new HashSet<>()
        currentParent = null

        String[] lines = gemfileLockContents.split('\n')
        for (String line : lines) {
            if (!line?.trim()) {
                inSpecsSection = false
                inDependenciesSection = false
                continue
            }

            if (!inSpecsSection && '  specs:' == line) {
                inSpecsSection = true
                continue
            }

            if (!inDependenciesSection && 'DEPENDENCIES' == line) {
                inDependenciesSection = true
                continue
            }

            if (!inSpecsSection && !inDependenciesSection) {
                continue
            }

            //we are now either in the specs section or in the dependencies section
            if (inSpecsSection) {
                parseSpecsSectionLine(line)
            } else {
                parseDependencySectionLine(line)
            }
        }

        directDependencyNames.each { directDependencyName ->
            NameVersionNode nameVersionNode = nameVersionNodeBuilder.nameToNodeMap[directDependencyName]
            if (nameVersionNode) {
                DependencyNode directDependencyNode = nameVersionNodeTransformer.createDependencyNode(Forge.RUBYGEMS, nameVersionNode)
                rootProject.children.add(directDependencyNode)
            } else {
                logger.debug("Could not find ${directDependencyName} in the populated map.")
            }
        }
    }

    private void parseSpecsSectionLine(String line) {
        if (line.startsWith('      ')) {
            if (!currentParent) {
                logger.error("Trying to add a child without a parent: ${line}")
            } else {
                NameVersionNode childNode = createNameVersionNode(line)
                nameVersionNodeBuilder.addChildNodeToParent(childNode, currentParent)
            }
        } else if (line.startsWith('    ')) {
            currentParent = createNameVersionNode(line)
            nameVersionNodeBuilder.addChildNodeToParent(currentParent, rootNameVersionNode)
        } else {
            logger.error("Line in specs section can't be parsed: ${line}")
        }
    }

    private void parseDependencySectionLine(String line) {
        NameVersionNode dependencyNameVersionNode = createNameVersionNode(line)
        if (!dependencyNameVersionNode.name) {
            logger.error("Line in dependencies section can't be parsed: ${line}")
        } else {
            directDependencyNames.add(dependencyNameVersionNode.name)
        }
    }

    private NameVersionNode createNameVersionNode(String line) {
        def name = line.trim()
        def version = ''
        int spaceIndex = name.indexOf(' ')
        if (spaceIndex > 0) {
            version = parseValidVersion(name[spaceIndex..-1].trim())
            name = name[0..spaceIndex].trim()
        }

        if (name.endsWith('!')) {
            name = name[0..-2]
        }
        new NameVersionNodeImpl([name: name, version: version])
    }

    //a valid version looks like (###.###.###)
    private String parseValidVersion(String version) {
        if (version[0] != '(' || version[-1] != ')' || version.indexOf('=') > 0 || version.indexOf('~') >= 0 ||  version.indexOf('>') >= 0 || version.indexOf('<') >= 0) {
            return ''
        } else {
            return version[1..-2]
        }
    }

}