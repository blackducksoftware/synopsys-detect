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
package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.NodeMetadata
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.builder.SubcomponentNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.SubcomponentMetadata
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

import groovy.transform.TypeChecked

@Component
@TypeChecked
class CocoapodsPackager {
    final List<String> fuzzyVersionIdentifiers = ['>', '<', '~>', '=']

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> extractDependencyNodes(final String podLockText) {
        YAMLMapper mapper = new YAMLMapper()
        PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class)

        def root = new NameVersionNodeImpl()
        root.name = "detectRootNode - ${UUID.randomUUID()}"
        def builder = new SubcomponentNodeBuilder(root)

        podfileLock.pods.each { buildNameVersionNode(builder, it) }

        podfileLock.dependencies.each {
            def child = new NameVersionNodeImpl([name: cleanPodName(it.name)])
            builder.addChildNodeToParent(child, root)
        }

        podfileLock.externalSources?.sources.each { source ->
            NodeMetadata nodeMetadata = createMetadata(builder, source.name)

            if (source.git && source.git.contains('github')) {
                // Change the forge to GitHub when there is better KB support
                nodeMetadata.setForge(Forge.COCOAPODS)
            } else if (source.path && source.path.contains('node_modules')) {
                nodeMetadata.setForge(Forge.NPM)
            }
        }

        builder.build().children.collect { nameVersionNodeTransformer.createDependencyNode(Forge.COCOAPODS, it as NameVersionNode) } as Set
    }

    private NameVersionNode buildNameVersionNode(SubcomponentNodeBuilder builder, Pod pod) {
        NameVersionNode nameVersionNode = new NameVersionNodeImpl()
        nameVersionNode.name = cleanPodName(pod.name)
        pod.cleanName = nameVersionNode.name
        String[] segments = pod.name.split(' ')
        if (segments.length > 1) {
            String version = segments[1]
            version = version.replace('(','').replace(')','').trim()
            if (!isVersionFuzzy(version)) {
                nameVersionNode.version = version
            }
        }

        pod.dependencies.each { builder.addChildNodeToParent(buildNameVersionNode(builder, new Pod(it)), nameVersionNode) }

        if (pod.dependencies.isEmpty()) {
            builder.addToCache(nameVersionNode)
        }

        if (nameVersionNode.name.contains('/')) {
            String superNodeName = nameVersionNode.name.split('/')[0].trim()
            def superNode = builder.addToCache(new NameVersionNodeImpl([name: superNodeName]))
            SubcomponentMetadata superNodeMetadata = createMetadata(builder, superNode.name)
            superNodeMetadata.subcomponents.add(nameVersionNode)

            SubcomponentMetadata subcomponentMetadata = createMetadata(builder, nameVersionNode.name)
            subcomponentMetadata.linkNode = superNode

            builder.superComponents.add(superNode)
        }

        nameVersionNode
    }

    private SubcomponentMetadata createMetadata(NameVersionNodeBuilder builder, String name) {
        SubcomponentMetadata metadata = builder.getNodeMetadata(cleanPodName(name)) as SubcomponentMetadata
        if (!metadata) {
            metadata = new SubcomponentMetadata()
            builder.setMetadata(name, metadata)
        }

        metadata
    }

    private boolean isVersionFuzzy(String versionName) {
        fuzzyVersionIdentifiers.any { versionName.contains(it) }
    }

    private String cleanPodName(String rawPodName) {
        rawPodName?.split(' ')[0].trim()
    }
}
