/*
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder

import groovy.transform.TypeChecked

@TypeChecked
class GemlockNodeParser {
    private final Logger logger = LoggerFactory.getLogger(GemlockNodeParser.class)

    private NameVersionNode rootNameVersionNode
    private NameVersionNodeBuilder nameVersionNodeBuilder
    private HashSet<String> directDependencyNames
    private NameVersionNode currentParent

    private boolean inSpecsSection = false
    private boolean inDependenciesSection = false
    private boolean previousLineWasBundledWith = false;

    DependencyGraph parseProjectDependencies(NameVersionNodeTransformer nameVersionNodeTransformer, final List<String> gemfileLockLines) {
        rootNameVersionNode = new NameVersionNodeImpl([name: 'gemfileLockRoot'])
        nameVersionNodeBuilder = new NameVersionNodeBuilder(rootNameVersionNode)
        directDependencyNames = new HashSet<>()
        currentParent = null

        gemfileLockLines.each { String line ->
            if (!line?.trim()) {
                inSpecsSection = false
                inDependenciesSection = false
                return
            }

            if (!inSpecsSection && 'specs:'.equals(line.trim())) {
                inSpecsSection = true
                return
            }

            if (!inDependenciesSection && 'DEPENDENCIES'.equals(line.trim())) {
                inDependenciesSection = true
                return
            }

            if ("BUNDLED WITH".equals(line.trim())) {
                previousLineWasBundledWith = true;
            } else if (previousLineWasBundledWith) {
                previousLineWasBundledWith = false;
                def bundler = nameVersionNodeBuilder.nodeCache["bundler"];
                bundler?.version = line.trim();
            }

            if (!inSpecsSection && !inDependenciesSection) {
                return
            }

            //we are now either in the specs section or in the dependencies section
            if (inSpecsSection) {
                parseSpecsSectionLine(line)
            } else {
                parseDependencySectionLine(line)
            }
        }

        MutableDependencyGraph graph = new MutableMapDependencyGraph()

        directDependencyNames.each { directDependencyName ->
            NameVersionNode nameVersionNode = nameVersionNodeBuilder.nodeCache[directDependencyName]
            if (nameVersionNode) {
                Dependency directDependency = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.RUBYGEMS, nameVersionNode)
                graph.addChildToRoot(directDependency)
            } else {
                logger.debug("Could not find ${directDependencyName} in the populated map.")
            }
        }

        graph
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