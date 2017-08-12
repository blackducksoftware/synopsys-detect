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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

@Component
class YarnPackager {
    private final Logger logger = LoggerFactory.getLogger(YarnPackager.class)

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> parse(String yarnLockText) {
        def rootNode = new NameVersionNodeImpl()
        rootNode.name = ''
        rootNode.version = ''
        def nameVersionNodeBuilder = new NameVersionNodeBuilder(rootNode)

        NameVersionNode currentNode = null
        boolean dependenciesStarted = false
        for (String line : yarnLockText.split(System.lineSeparator())) {
            if (!line.trim()) {
                continue
            }

            if (line.trim().startsWith('#')) {
                continue
            }

            int level = getLineLevel(line)
            if (level == 0) {
                currentNode = lineToNameVersionNode(nameVersionNodeBuilder, rootNode, line)
                dependenciesStarted = false
                continue
            }

            if(level == 1 && line.trim().startsWith('version')) {
                currentNode.version = line.trim().split(' ')[1].replaceAll('"','').trim()
                continue
            }

            if(level == 1 && line.trim() == 'dependencies:') {
                dependenciesStarted = true
                continue
            }

            if(level == 2 && dependenciesStarted) {
                NameVersionNode dependency = dependencyLineToNameVersionNode(line)
                nameVersionNodeBuilder.addChildNodeToParent(dependency, currentNode)
                continue
            }
        }

        Stack<String> cyclicalStack = new Stack<>()
        def nodes = rootNode.children.collect { nameVersionNodeLinkedTransformer(cyclicalStack, it) } as Set

        nodes
    }

    private int getLineLevel(String line) {
        int level = 0
        while (line.startsWith('  ')) {
            line = line.replaceFirst('  ', '')
            level++
        }

        level
    }

    // Example: "mime-types@^2.1.12" becomes "mime-types"
    private String cleanFuzzyName(String fuzzyName) {
        String cleanName = fuzzyName.replaceAll('"', '')
        String version = cleanName.split('@')[-1]
        String name = cleanName[0..cleanName.indexOf(version) - 2]

        name
    }

    private NameVersionNode dependencyLineToNameVersionNode(String line) {
        final NameVersionNode nameVersionNode = new NameVersionNodeImpl()
        nameVersionNode.name = line.trim().replace(' ', '@').replaceAll('"', '')

        nameVersionNode
    }

    private NameVersionNode lineToNameVersionNode(NameVersionNodeBuilder nameVersionNodeBuilder, NameVersionNode root, String line) {
        String cleanLine = line.replaceAll('"', '')
        List<String> fuzzyNames = line.split(',').collect { it.trim() }

        NameVersionNode linkedNameVersionNode = new NameVersionNodeImpl()
        linkedNameVersionNode.name = cleanFuzzyName(fuzzyNames[0])

        fuzzyNames.each {
            def nameVersionNode = new NameVersionNodeImpl()
            nameVersionNode.name = it.trim().replace(':', '')
            nameVersionNode.link = linkedNameVersionNode
            nameVersionNodeBuilder.addChildNodeToParent(nameVersionNode, root)
        }

        linkedNameVersionNode
    }

    private DependencyNode nameVersionNodeLinkedTransformer(Stack<String> cyclicalStack, NameVersionNode nameVersionNode) {
        NameVersionNode link = nameVersionNode.getLink()
        String name = nameVersionNode.link ? link.name : nameVersionNode.name
        String version = nameVersionNode.link ? link.version : nameVersionNode.version
        List<NameVersionNode> children = nameVersionNode.link ? link.children : nameVersionNode.children

        if(cyclicalStack.contains(name)) {
            return null
        }
        cyclicalStack.push(name)

        def externalId = new NameVersionExternalId(Forge.NPM, name, version)
        def dependencyNode = new DependencyNode(name, version, externalId)

        children.each {
            DependencyNode child = nameVersionNodeLinkedTransformer(cyclicalStack, it)
            if(child) {
                dependencyNode.children.add(child)
            } else {
                logger.info("Cyclical depdency [${it.name}] detected")
            }
        }
        cyclicalStack.pop()

        dependencyNode
    }
}
