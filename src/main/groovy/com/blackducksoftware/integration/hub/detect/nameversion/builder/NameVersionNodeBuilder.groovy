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
package com.blackducksoftware.integration.hub.detect.nameversion.builder

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NodeMetadata

import groovy.transform.TypeChecked

/**
 * This class should be used to construct node graphs where you don't
 * always have a defined version for each dependency, but will EVENTUALLY find
 * a defined version, as in Gemfile.lock files.
 */
@TypeChecked
class NameVersionNodeBuilder {
    final Logger logger = LoggerFactory.getLogger(NameVersionNodeBuilder.class)

    final Map<String, NameVersionNode> nodeCache = [:]
    final NameVersionNode root

    public NameVersionNodeBuilder(final NameVersionNode root) {
        this.root = root
        nodeCache.put(root.name, root)
    }

    public void addChildNodeToParent(final NameVersionNode child, final NameVersionNode parent) {
        addToCache(child)
        addToCache(parent)

        nodeCache[parent.name].children.add(nodeCache[child.name])
    }

    public NameVersionNode addToCache(final NameVersionNode nameVersionNode) {
        if (!nameVersionNode.name?.trim()) {
            logger.debug("A component must have a name to be added to the graph. The supplied component is invalid: ${nameVersionNode.toString()}")
            return null
        }

        if (!nodeCache.containsKey(nameVersionNode.name)) {
            nodeCache.put(nameVersionNode.name, nameVersionNode)
        }

        if (nameVersionNode.version?.trim() && !nodeCache[nameVersionNode.name].version?.trim()) {
            nodeCache[nameVersionNode.name].version = nameVersionNode.version
        }

        if (nameVersionNode.metadata && !nodeCache[nameVersionNode.name].metadata) {
            nodeCache[nameVersionNode.name].metadata = nameVersionNode.metadata
        }

        nodeCache[nameVersionNode.name]
    }

    public NodeMetadata getNodeMetadata(String nodeName) {
        nodeCache[nodeName]?.getMetadata()
    }

    public void setMetadata(String nodeName, NodeMetadata metadata) {
        nodeCache[nodeName]?.setMetadata(metadata)
    }

    public NameVersionNode build() {
        root
    }
}
