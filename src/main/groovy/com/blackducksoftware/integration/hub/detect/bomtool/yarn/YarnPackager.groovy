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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata
import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@TypeChecked
class YarnPackager {
    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public DependencyGraph parseYarnLock(List<String> yarnLockText) {
        def rootNode = new NameVersionNode()
        rootNode.name = "detectRootNode - ${UUID.randomUUID()}"
        def nameVersionLinkNodeBuilder = new LinkedNameVersionNodeBuilder(rootNode)

        NameVersionNode currentNode = null
        boolean dependenciesStarted = false
        for (String line : yarnLockText) {
            if (!line.trim()) {
                continue
            }

            if (line.trim().startsWith('#')) {
                continue
            }

            int level = getLineLevel(line)
            if (level == 0) {
                currentNode = lineToNameVersionNode(nameVersionLinkNodeBuilder, rootNode, line.trim())
                dependenciesStarted = false
                continue
            }

            if (level == 1 && line.trim().startsWith('version')) {
                String fieldName = line.trim().split(' ')[0]
                currentNode.version = line.trim().substring(fieldName.length()).replaceAll('"', '').trim()
                continue
            }

            if (level == 1 && line.trim() == 'dependencies:') {
                dependenciesStarted = true
                continue
            }

            if (level == 2 && dependenciesStarted) {
                NameVersionNode dependency = dependencyLineToNameVersionNode(line)
                nameVersionLinkNodeBuilder.addChildNodeToParent(dependency, currentNode)
                continue
            }
        }

        MutableDependencyGraph graph = new MutableMapDependencyGraph()

        nameVersionLinkNodeBuilder.build().children.each {
            Dependency root = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.NPM, it)
            graph.addChildToRoot(root)
        }

        graph
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

    private NameVersionNode dependencyLineToNameVersionNode(String line) {
        final NameVersionNode nameVersionNode = new NameVersionNode()
        nameVersionNode.name = line.trim().replaceFirst(' ', '@').replace('"', '')

        nameVersionNode
    }

    private NameVersionNode lineToNameVersionNode(NameVersionNodeBuilder nameVersionNodeBuilder, NameVersionNode root, String line) {
        String cleanLine = line.replace('"', '').replace(':', '')
        List<String> fuzzyNames = cleanLine.split(',').collect { String name -> name.trim() }

        if (fuzzyNames.isEmpty()) {
            return null
        }

        String name = cleanFuzzyName(fuzzyNames[0] as String)

        NameVersionNode linkedNameVersionNode = new NameVersionNode()
        linkedNameVersionNode.name = cleanFuzzyName(fuzzyNames[0] as String)

        fuzzyNames.each {
            def nameVersionLinkNode = new NameVersionNode()
            nameVersionLinkNode.name = it
            nameVersionLinkNode.metadata = new LinkMetadata(linkNode: linkedNameVersionNode)
            nameVersionNodeBuilder.addChildNodeToParent(nameVersionLinkNode, root)
        }

        linkedNameVersionNode
    }

    DependencyGraph parseYarnList() {
        // TODO write tests to parse a 'yarn list --prod' call. Tests can be similar to NPM ones
        null
    }
}
