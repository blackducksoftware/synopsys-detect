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
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.util.NameVersionNode
import com.blackducksoftware.integration.hub.detect.util.NameVersionNodeBuilder

class PipInspectorTreeParser {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    public static final String SEPERATOR = '=='
    public static final String UNKOWN_PROJECT = "n?${SEPERATOR}v?"
    public static final String UNKOWN_REQUIREMENTS_PREFIX = 'r?'
    public static final String UNKOWN_PACKAGE_PREFIX = '--'

    DependencyNode parse(String treeText) {
        def lines = treeText.trim().split('\n').toList()

        NameVersionNode projectNode = lineToNode(lines.get(0))
        lines.remove(0)
        def nodeBuilder = new NameVersionNodeBuilder(projectNode)

        Stack<NameVersionNode> tree = new Stack<>()
        tree.push(projectNode)

        int indentation = 0
        for(String line: lines) {
            if(line.startsWith(UNKOWN_REQUIREMENTS_PREFIX)) {
                String path = line.replace(UNKOWN_REQUIREMENTS_PREFIX).trim()
                logger.info("Pip inspector could not locate requirements file @ ${path}")
                continue
            }

            if(line.startsWith(UNKOWN_PACKAGE_PREFIX)) {
                String packageName = line.replace(UNKOWN_PACKAGE_PREFIX).trim()
                logger.info("Pip inspector could not resolve the package: ${packageName}")
                continue
            }

            int currentIndentation = getCurrentIndentation(line)
            NameVersionNode node = lineToNode(line)
            if (currentIndentation == indentation) {
                tree.pop()
            } else {
                for(;indentation >= currentIndentation; indentation--) {
                    tree.pop()
                }
            }

            nodeBuilder.addChildNodeToParent(node, tree.peek())
            indentation = currentIndentation
            tree.push(node)
        }

        transformToDependencyNode(nodeBuilder.getRoot())
    }

    DependencyNode transformToDependencyNode(NameVersionNode node) {
        ExternalId externalId = new NameVersionExternalId(Forge.PYPI, node.name, node.version)
        DependencyNode newNode = new DependencyNode(node.name, node.version, externalId)
        for(NameVersionNode child: node.children) {
            def childDependencyNode = transformToDependencyNode(child)
            newNode.children.add(childDependencyNode)
        }

        newNode
    }

    NameVersionNode lineToNode(String line) {
        def segments = line.split(SEPERATOR)
        def node = new NameVersionNode()
        node.name = segments[0].trim()
        node.version = segments[1].trim()

        node
    }

    int getCurrentIndentation(String line) {
        int currentIndentation = 0
        while(line.startsWith(' '.multiply(4))) {
            currentIndentation++
            line = line.substring(4)
        }

        currentIndentation
    }
}
