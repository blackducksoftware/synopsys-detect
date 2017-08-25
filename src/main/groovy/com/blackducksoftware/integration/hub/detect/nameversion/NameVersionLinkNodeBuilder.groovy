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
package com.blackducksoftware.integration.hub.detect.nameversion

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * This class should be used to construct node graphs where you don't
 * always have a defined version for each dependency, but will EVENTUALLY find
 * a defined version, as in Gemfile.lock files.
 */
class NameVersionLinkNodeBuilder {
    final Logger logger = LoggerFactory.getLogger(NameVersionLinkNodeBuilder.class)

    final Map<String, NameVersionLinkNode> nameToNodeMap = [:]
    final NameVersionLinkNode root

    public NameVersionLinkNodeBuilder(final NameVersionLinkNode root) {
        this.root = root
        nameToNodeMap.put(root.name, root)
    }

    public void addChildNodeToParent(final NameVersionLinkNode child, final NameVersionLinkNode parent) {
        if (!nameToNodeMap.containsKey(child.name)) {
            nameToNodeMap.put(child.name, child)
        }

        if (!nameToNodeMap.containsKey(parent.name)) {
            nameToNodeMap.put(parent.name, parent)
        }

        if (child.version?.trim() && !nameToNodeMap[child.name].version?.trim()) {
            nameToNodeMap[child.name].version = child.version
        }

        if (parent.version?.trim() && !nameToNodeMap[parent.name].version?.trim()) {
            nameToNodeMap[parent.name].version = parent.version
        }

        if (child.link && !nameToNodeMap[child.name].link) {
            nameToNodeMap[child.name].link = child.link
        }

        if (parent.link && !nameToNodeMap[parent.name].link) {
            nameToNodeMap[parent.name].link = parent.link
        }

        nameToNodeMap[parent.name].children.add(nameToNodeMap[child.name])
    }

    public NameVersionNode build() {
        Stack<String> cyclicalStack = new Stack<>()

        resolveLink(cyclicalStack, root)
    }

    private NameVersionNode resolveLink(final Stack<String> cyclicalStack, final NameVersionLinkNode nameVersionLinkNode) {
        if (cyclicalStack.contains(nameVersionLinkNode.name)) {
            logger.debug("Cyclical depdency detected: ${nameVersionLinkNode.name}")
            return null
        }
        cyclicalStack.push(nameVersionLinkNode.name)

        NameVersionNode nameVersionNode = null
        if (nameVersionLinkNode.link) {
            nameVersionNode = resolveLink(cyclicalStack, nameVersionLinkNode.link)
        }

        if (!nameVersionNode && (nameVersionLinkNode.version || nameVersionLinkNode == root)) {
            nameVersionNode = new NameVersionNodeImpl()
            nameVersionNode.name = nameVersionLinkNode.name
            nameVersionNode.version = nameVersionLinkNode.version
            nameVersionLinkNode.children.each {
                NameVersionNode child = resolveLink(cyclicalStack, it)
                if (child) {
                    nameVersionNode.children.add(child)
                }
            }
        }

        cyclicalStack.pop()

        nameVersionNode
    }
}
