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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

class PipInspectorTreeParser {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    public static final String SEPERATOR = '=='
    public static final String UNKOWN_PROJECT_NAME = "n?"
    public static final String UNKOWN_PROJECT_VERSION = "v?"
    public static final String UNKOWN_PROJECT = UNKOWN_PROJECT_NAME + SEPERATOR + UNKOWN_PROJECT_VERSION
    public static final String UNKOWN_REQUIREMENTS_PREFIX = 'r?'
    public static final String UNKOWN_PACKAGE_PREFIX = '--'
    public static final String INDENTATION = ' '.multiply(4)

    DependencyNode parse(NameVersionNodeTransformer nameVersionNodeTransformer, String treeText) {
        def lines = treeText.trim().split('\n').toList()

        def nodeBuilder = null
        Stack<NameVersionNode> tree = new Stack<>()

        int indentation = 0
        for (String line: lines) {
            if (!line.trim()) {
                continue
            }

            if (line.startsWith(UNKOWN_REQUIREMENTS_PREFIX)) {
                String path = line.replace(UNKOWN_REQUIREMENTS_PREFIX, '').trim()
                logger.info("Pip inspector could not locate requirements file @ ${path}")
                continue
            }

            if (line.startsWith(UNKOWN_PACKAGE_PREFIX)) {
                String packageName = line.replace(UNKOWN_PACKAGE_PREFIX, '').trim()
                logger.info("Pip inspector could not resolve the package: ${packageName}")
                continue
            }

            if (line.contains(SEPERATOR) && !nodeBuilder) {
                NameVersionNode projectNode = lineToNode(line)
                tree.push(projectNode)
                nodeBuilder = new NameVersionNodeBuilder(projectNode)
                continue
            }

            if (!nodeBuilder) {
                continue
            }

            int currentIndentation = getCurrentIndentation(line)
            NameVersionNode node = lineToNode(line)
            if (currentIndentation == indentation) {
                tree.pop()
            } else {
                for (;indentation >= currentIndentation; indentation--) {
                    tree.pop()
                }
            }

            nodeBuilder.addChildNodeToParent(node, tree.peek())
            indentation = currentIndentation
            tree.push(node)
        }

        if (nodeBuilder) {
            NameVersionNode projectNode = nodeBuilder.getRoot()
            if (projectNode.name == UNKOWN_PROJECT_NAME && projectNode.version == UNKOWN_PROJECT_VERSION) {
                projectNode.name == null
                projectNode.version == null
            }
            return nameVersionNodeTransformer.createDependencyNode(Forge.PYPI, projectNode)
        }

        null
    }

    NameVersionNode lineToNode(String line) {
        if (!line.contains(SEPERATOR)) {
            return null
        }
        def segments = line.split(SEPERATOR)
        def node = new NameVersionNodeImpl()
        node.name = segments[0].trim()
        node.version = segments[1].trim()

        node
    }

    int getCurrentIndentation(String line) {
        int currentIndentation = 0
        while(line.startsWith(INDENTATION)) {
            currentIndentation++
            line = line.replaceFirst(INDENTATION, '')
        }

        currentIndentation
    }
}
