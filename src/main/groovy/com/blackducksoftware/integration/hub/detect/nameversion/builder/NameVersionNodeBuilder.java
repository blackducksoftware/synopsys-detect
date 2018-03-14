/**
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
package com.blackducksoftware.integration.hub.detect.nameversion.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NodeMetadata;

/**
 * This class should be used to construct node graphs where you don't always have a defined version for each dependency, but will EVENTUALLY find a defined version, as in Gemfile.lock files.
 */
public class NameVersionNodeBuilder {
    protected final Logger logger = LoggerFactory.getLogger(NameVersionNodeBuilder.class);

    protected final Map<String, NameVersionNode> nodeCache = new HashMap<>();
    protected final NameVersionNode root;

    public NameVersionNodeBuilder(final NameVersionNode root) {
        this.root = root;
        nodeCache.put(root.getName(), root);
    }

    public void addChildNodeToParent(final NameVersionNode child, final NameVersionNode parent) {
        addToCache(child);
        addToCache(parent);

        nodeCache.get(parent.getName()).getChildren().add(nodeCache.get(child.getName()));
    }

    public NameVersionNode addToCache(final NameVersionNode nameVersionNode) {
        if (StringUtils.isBlank(nameVersionNode.getName())) {
            logger.debug(String.format("A component must have a name to be added to the graph. The supplied component is invalid: %s", nameVersionNode.toString()));
            return null;
        }

        if (!nodeCache.containsKey(nameVersionNode.getName())) {
            nodeCache.put(nameVersionNode.getName(), nameVersionNode);
        }

        if (StringUtils.isNotBlank(nameVersionNode.getVersion()) && StringUtils.isBlank(nodeCache.get(nameVersionNode.getName()).getVersion())) {
            nodeCache.get(nameVersionNode.getName()).setVersion(nameVersionNode.getVersion());
        }

        if (nameVersionNode.getMetadata() != null && nodeCache.get(nameVersionNode.getName()).getMetadata() == null) {
            nodeCache.get(nameVersionNode.getName()).setMetadata(nameVersionNode.getMetadata());
        }

        return nodeCache.get(nameVersionNode.getName());
    }

    public NodeMetadata getNodeMetadata(final String nodeName) {
        if (nodeCache.containsKey(nodeName)) {
            return nodeCache.get(nodeName).getMetadata();
        }
        return null;
    }

    public void setMetadata(final String nodeName, final NodeMetadata metadata) {
        final NameVersionNode node = nodeCache.get(nodeName);
        if (node != null) {
            node.setMetadata(metadata);
        }
    }

    public NameVersionNode build() {
        return root;
    }

}
