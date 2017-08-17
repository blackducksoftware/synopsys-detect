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
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionLinkNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionLinkNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

@Component
class YarnPackager {
    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> parse(String yarnLockText) {
        def rootNode = new NameVersionLinkNode()
        rootNode.name = ''
        rootNode.version = ''
        def nameVersionLinkNodeBuilder = new NameVersionLinkNodeBuilder(rootNode)

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
                currentNode = lineToNameVersionLinkNode(nameVersionLinkNodeBuilder, rootNode, line)
                dependenciesStarted = false
                continue
            }

            if(level == 1 && line.trim().startsWith('version')) {
                String fieldName = line.trim().split(' ')[0]
                currentNode.version = line.trim().substring(fieldName.length()).replaceAll('"','').trim()
                continue
            }

            if(level == 1 && line.trim() == 'dependencies:') {
                dependenciesStarted = true
                continue
            }

            if(level == 2 && dependenciesStarted) {
                NameVersionLinkNode dependency = dependencyLineToNameVersionLinkNode(line)
                nameVersionLinkNodeBuilder.addChildNodeToParent(dependency, currentNode)
                continue
            }
        }

        nameVersionLinkNodeBuilder.build().children.collect { nameVersionNodeTransformer.createDependencyNode(Forge.NPM, it) } as Set
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
        String cleanName = fuzzyName.replace('"', '')
        String version = cleanName.split('@')[-1]
        String name = cleanName[0..cleanName.indexOf(version) - 2].trim()

        name
    }

    private NameVersionLinkNode dependencyLineToNameVersionLinkNode(String line) {
        final NameVersionLinkNode nameVersionNode = new NameVersionLinkNode()
        nameVersionNode.name = line.trim().replaceFirst(' ', '@').replace('"', '')

        nameVersionNode
    }

    private NameVersionLinkNode lineToNameVersionLinkNode(NameVersionLinkNodeBuilder nameVersionLinkNodeBuilder, NameVersionLinkNode root, String line) {
        String cleanLine = line.replace('"', '').replace(':', '')
        List<String> fuzzyNames = cleanLine.split(',').collect { it.trim() }

        if (fuzzyNames.isEmpty()) {
            return null
        }

        NameVersionLinkNode linkedNameVersionNode = new NameVersionLinkNode()
        linkedNameVersionNode.name = cleanFuzzyName(fuzzyNames[0])

        fuzzyNames.each {
            def nameVersionLinkNode = new NameVersionLinkNode()
            nameVersionLinkNode.name = it
            nameVersionLinkNode.link = linkedNameVersionNode
            nameVersionLinkNodeBuilder.addChildNodeToParent(nameVersionLinkNode, root)
        }

        linkedNameVersionNode
    }
}
