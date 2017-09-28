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
package com.blackducksoftware.integration.hub.detect.bomtool.pip

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalIdFactory

import groovy.transform.TypeChecked

@Component
@TypeChecked
class PipInspectorTreeParser {
    final Logger logger = LoggerFactory.getLogger(PipInspectorTreeParser.class)

    public static final String SEPARATOR = '=='
    public static final String UNKNOWN_PROJECT_NAME = 'n?'
    public static final String UNKNOWN_PROJECT_VERSION = 'v?'
    public static final String UNKNOWN_REQUIREMENTS_PREFIX = 'r?'
    public static final String UNPARSEABLE_REQUIREMENTS_PREFIX = 'p?'
    public static final String UNKNOWN_PACKAGE_PREFIX = '--'
    public static final String INDENTATION = ' '.multiply(4)

    @Autowired
    ExternalIdFactory externalIdFactory

    DependencyNode parse(String treeText) {
        def lines = treeText.trim().split(System.lineSeparator()).toList()

        DependencyNodeBuilder dependencyNodeBuilder = null
        Stack<DependencyNode> tree = new Stack<>()

        int indentation = 0
        for (String line: lines) {
            if (!line.trim()) {
                continue
            }

            if (line.trim().startsWith(UNKNOWN_REQUIREMENTS_PREFIX)) {
                String path = line.replace(UNKNOWN_REQUIREMENTS_PREFIX, '').trim()
                logger.info("Pip inspector could not find requirements file @ ${path}")
                continue
            }

            if (line.trim().startsWith(UNPARSEABLE_REQUIREMENTS_PREFIX)) {
                String path = line.replace(UNPARSEABLE_REQUIREMENTS_PREFIX, '').trim()
                logger.info("Pip inspector could not parse requirements file @ ${path}")
                continue
            }

            if (line.trim().startsWith(UNKNOWN_PACKAGE_PREFIX)) {
                String packageName = line.replace(UNKNOWN_PACKAGE_PREFIX, '').trim()
                logger.info("Pip inspector could not resolve the package: ${packageName}")
                continue
            }

            if (line.contains(SEPARATOR) && !dependencyNodeBuilder) {
                DependencyNode projectNode = lineToNode(line)
                tree.push(projectNode)
                dependencyNodeBuilder = new DependencyNodeBuilder(projectNode)
                continue
            }

            if (!dependencyNodeBuilder) {
                continue
            }

            int currentIndentation = getCurrentIndentation(line)
            DependencyNode node = lineToNode(line)
            if (currentIndentation == indentation) {
                tree.pop()
            } else {
                for (;indentation >= currentIndentation; indentation--) {
                    tree.pop()
                }
            }
            dependencyNodeBuilder.addChildNodeWithParents(node, [tree.peek()])
            indentation = currentIndentation
            tree.push(node)
        }

        if (dependencyNodeBuilder) {
            return dependencyNodeBuilder.root
        }

        null
    }

    DependencyNode lineToNode(String line) {
        if (!line.contains(SEPARATOR)) {
            return null
        }
        def segments = line.split(SEPARATOR)
        String name = segments[0].trim()
        name = name.equals(UNKNOWN_PROJECT_NAME) ? '' : name
        String version = segments[1].trim()
        version = version.equals(UNKNOWN_PROJECT_VERSION) ? '' : version
        def externalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version)
        def node = new DependencyNode(name, version, externalId)

        node
    }

    int getCurrentIndentation(String line) {
        int currentIndentation = 0
        while (line.startsWith(INDENTATION)) {
            currentIndentation++
            line = line.replaceFirst(INDENTATION, '')
        }

        currentIndentation
    }
}
