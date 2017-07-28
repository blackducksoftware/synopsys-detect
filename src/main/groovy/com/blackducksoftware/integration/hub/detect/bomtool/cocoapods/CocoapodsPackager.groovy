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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

@Component
class CocoapodsPackager {
    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> extractDependencyNodes(final String podLockText) {
        List<NameVersionNode> nameVersionNodes = parse(podLockText)

        nameVersionNodes.collect { nameVersionNodeTransformer.createDependencyNode(Forge.COCOAPODS, it) } as Set
    }

    private List<NameVersionNode> parse(final String podLockText) {
        final List<NameVersionNode> directDependencies = []
        final NameVersionNode root = new NameVersionNodeImpl()
        root.name = 'hub_detect_root'
        final def nameVersionNodeBuilder = new NameVersionNodeBuilder(root)

        NameVersionNode parentNode = root
        boolean podsSection = false
        boolean dependenciesSection = false
        for (String line: podLockText.split(System.getProperty('line.separator'))) {
            if (!line.trim()) {
                continue
            }

            if (!podsSection && line.trim() == 'PODS:') {
                dependenciesSection = false
                podsSection = true
                continue
            }

            if (!dependenciesSection && line.trim() == 'DEPENDENCIES:') {
                podsSection = false
                dependenciesSection = true
                continue
            }

            int lineLevel = getLevel(line)
            if (lineLevel == 0) {
                podsSection = false
                dependenciesSection = false
            }

            if (!podsSection && !dependenciesSection) {
                continue
            }

            NameVersionNode pod = lineToNameVersionNode(line)
            if (podsSection) {
                if (lineLevel == 1) {
                    nameVersionNodeBuilder.addChildNodeToParent(pod, root)
                    parentNode = pod
                } else {
                    if(parentNode.name != pod.name) {
                        nameVersionNodeBuilder.addChildNodeToParent(pod, parentNode)
                    }
                }
            } else if (dependenciesSection) {
                directDependencies.add(pod)
            }
        }
        Map<String, NameVersionNode> allDependendencies = nameVersionNodeBuilder.getNameToNodeMap()

        directDependencies.collect { allDependendencies[it.name] }
    }

    private NameVersionNode lineToNameVersionNode(final String line) {
        String clean = line.replaceFirst('- ', '').replace(':','')
        String version = null
        boolean fuzzyVersion = clean.contains('~>') || clean.contains('>') || clean.contains('=') || clean.contains('<')
        if (clean.contains('(') && clean.contains(')') && !fuzzyVersion) {
            version = clean.substring(clean.indexOf('(') + 1, clean.indexOf(')' - 1)).trim()
        }
        clean = clean.replaceAll('\\(.*\\)', '').trim()
        def nameVersionNode = new NameVersionNodeImpl()
        // Grab the first section to aggregate sub-pods
        nameVersionNode.name = clean.split('/')[0].trim()
        nameVersionNode.version = version

        nameVersionNode
    }

    private int getLevel(String line) {
        if (line.startsWith('  - ')) {
            return 1
        } else if (line.startsWith('    - ')) {
            return 2
        }
        return 0
    }
}
