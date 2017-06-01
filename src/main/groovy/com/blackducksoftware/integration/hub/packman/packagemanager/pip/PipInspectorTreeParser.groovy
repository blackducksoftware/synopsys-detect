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
package com.blackducksoftware.integration.hub.packman.packagemanager.pip

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.util.NameVersionNode
import com.blackducksoftware.integration.hub.packman.util.NameVersionNodeBuilder

class PipInspectorTreeParser {

    DependencyNode parse(String treeText) {
        def lines = treeText.trim().split('\n').toList()

        NameVersionNode projectNode = lineToNode(lines.get(0))
        lines.remove(0)
        def nodeBuilder = new NameVersionNodeBuilder(projectNode)

        Stack<NameVersionNode> tree = new Stack<>()
        tree.push(projectNode)

        int level = 0
        for(String line: lines) {
            int currentLevel = getCurrentLevel(line)
            NameVersionNode node = lineToNode(line)
            if (currentLevel == level) {
                tree.pop()
            } else {
                for(;level >= currentLevel; level--) {
                    tree.pop()
                }
            }

            nodeBuilder.addChildNodeToParent(node, tree.peek())
            level = currentLevel
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
        def segments = line.split('==')
        def node = new NameVersionNode()
        node.name = segments[0].trim()
        node.version = segments[1].trim()

        node
    }

    int getCurrentLevel(String line) {
        int currentLevel = 0
        while(line.startsWith(' '.multiply(4))) {
            currentLevel++
            line = line.substring(4)
        }

        currentLevel
    }
}
