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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

@Component
class YarnPackager {
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

        rootNode.children.collect { nameVersionNodeTransformer.createDependencyNode(Forge.NPM, it) } as Set
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
}
