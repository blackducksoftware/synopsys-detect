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
    public static final String UNKOWN_PROJECT_NAME = "n?"
    public static final String UNKOWN_PROJECT_VERSION = "v?"
    public static final String UNKOWN_PROJECT = UNKOWN_PROJECT_NAME + SEPERATOR + UNKOWN_PROJECT_VERSION
    public static final String UNKOWN_REQUIREMENTS_PREFIX = 'r?'
    public static final String UNKOWN_PACKAGE_PREFIX = '--'

    DependencyNode parse(String treeText) {
        def lines = treeText.trim().split('\n').toList()

        def nodeBuilder = null
        Stack<NameVersionNode> tree = new Stack<>()

        int indentation = 0
        for(String line: lines) {
            if(!line.trim()) {
                continue
            }

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

            if(line.contains(SEPERATOR) && !nodeBuilder) {
                NameVersionNode projectNode = lineToNode(line)
                tree.push(projectNode)
                nodeBuilder = new NameVersionNodeBuilder(projectNode)
                continue
            }

            if(!nodeBuilder) {
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
