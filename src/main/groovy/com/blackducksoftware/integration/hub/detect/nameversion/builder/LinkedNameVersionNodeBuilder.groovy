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
package com.blackducksoftware.integration.hub.detect.nameversion.builder

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata

class LinkedNameVersionNodeBuilder extends NameVersionNodeBuilderImpl {

    public LinkedNameVersionNodeBuilder(NameVersionNode root) {
        super(root)
    }

    @Override
    public NameVersionNode build() {
        Stack<String> cyclicalStack = new Stack<>()
        Set<String> cyclicalNames = new HashSet<>()

        NameVersionNode resolved = resolveLinks(cyclicalNames, cyclicalStack, root)
        cyclicalNames.each { logger.debug("Cyclical depdency detected: ${it}") }

        resolved
    }

    private NameVersionNode resolveLinks(Set<String> cyclicalNames, Stack<String> cyclicalStack, NameVersionNode nameVersionNode) {
        if (!nameVersionNode) {
            return null
        }

        String name = nameVersionNode.getName()

        if (cyclicalStack.contains(nameVersionNode.name)) {
            cyclicalNames.add(nameVersionNode.name)
            return null
        }
        cyclicalStack.push(nameVersionNode.name)

        NameVersionNode resolvedNode = nameVersionNode
        LinkMetadata linkMetadata = getLinkMetadata(nameVersionNode)
        if (linkMetadata?.linkNode) {
            resolvedNode = resolveLinks(cyclicalNames, cyclicalStack, linkMetadata.linkNode)
        }

        if (resolvedNode) {
            List<NameVersionNode> resolvedChildren = []
            for (NameVersionNode child : resolvedNode.children) {
                NameVersionNode resolvedChild = resolveLinks(cyclicalNames, cyclicalStack, child)
                if (resolvedChild) {
                    resolvedChildren.add(resolvedChild)
                }
            }
            resolvedNode.children = resolvedChildren
        }
        cyclicalStack.pop()

        resolvedNode
    }

    private LinkMetadata getLinkMetadata(NameVersionNode nameVersionNode) {
        if (nameVersionNode.metadata instanceof LinkMetadata) {
            return nameVersionNode.metadata as LinkMetadata
        }

        null
    }
}
