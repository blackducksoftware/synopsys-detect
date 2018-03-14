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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata;

/**
 * This class allows you to link one node to another using the LinkMetadata class This means when the builder builds the graph, if a node has a link in its metadata, it will return the link as the resolved node instead of the original. This
 * class can also be used for package managers that allow cyclical dependencies
 */
public class LinkedNameVersionNodeBuilder extends NameVersionNodeBuilder {
    public LinkedNameVersionNodeBuilder(final NameVersionNode root) {
        super(root);
    }

    @Override
    public NameVersionNode build() {
        final Stack<String> cyclicalStack = new Stack<>();
        final Set<String> cyclicalNames = new HashSet<>();

        final NameVersionNode resolved = resolveLinks(cyclicalNames, cyclicalStack, root);
        for (final String cyclicalName : cyclicalNames) {
            logger.debug(String.format("Cyclical depdency detected: %s", cyclicalName));
        }

        return resolved;
    }

    private NameVersionNode resolveLinks(final Set<String> cyclicalNames, final Stack<String> cyclicalStack, final NameVersionNode nameVersionNode) {
        if (nameVersionNode == null) {
            return null;
        }

        final String name = nameVersionNode.getName();
        if (cyclicalStack.contains(name)) {
            cyclicalNames.add(name);
            return null;
        }
        cyclicalStack.push(name);

        NameVersionNode resolvedNode = nameVersionNode;
        final LinkMetadata linkMetadata = getLinkMetadata(nameVersionNode);
        if (linkMetadata != null && linkMetadata.getLinkNode() != null) {
            resolvedNode = resolveLinks(cyclicalNames, cyclicalStack, linkMetadata.getLinkNode());
        }

        if (resolvedNode != null) {
            final List<NameVersionNode> resolvedChildren = new ArrayList<>();
            for (final NameVersionNode child : resolvedNode.getChildren()) {
                final NameVersionNode resolvedChild = resolveLinks(cyclicalNames, cyclicalStack, child);
                if (resolvedChild != null) {
                    resolvedChildren.add(resolvedChild);
                }
            }
            resolvedNode.setChildren(resolvedChildren);
        }
        cyclicalStack.pop();

        return resolvedNode;
    }

    private LinkMetadata getLinkMetadata(final NameVersionNode nameVersionNode) {
        if (nameVersionNode.getMetadata() instanceof LinkMetadata) {
            return (LinkMetadata) nameVersionNode.getMetadata();
        }

        return null;
    }
}
