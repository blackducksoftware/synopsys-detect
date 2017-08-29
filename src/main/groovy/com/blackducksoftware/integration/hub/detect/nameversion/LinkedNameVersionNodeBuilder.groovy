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
package com.blackducksoftware.integration.hub.detect.nameversion

class LinkedNameVersionNodeBuilder extends NameVersionNodeBuilder {

    public LinkedNameVersionNodeBuilder(NameVersionNode root) {
        super(root)
    }

    //    @Override
    //    public NameVersionNode addToCache(final NameVersionNode nameVersionNode) {
    //        NameVersionNode added = super.addToCache(nameVersionNode)
    //
    //        LinkMetadata linkMetadata = getLinkMetadata(added)
    //        if (linkMetadata?.linkNode) {
    //            added = addToCache(linkMetadata.linkNode)
    //        }
    //
    //        added
    //    }

    @Override
    public NameVersionNode build() {
        Stack<NameVersionNode> cyclicalStack = new Stack<>()

        resolveLinks(cyclicalStack, root)
    }

    private NameVersionNode resolveLinks(Stack<String> cyclicalStack, NameVersionNode nameVersionNode) {
        if (!nameVersionNode) {
            return null
        }

        if (cyclicalStack.contains(nameVersionNode.name)) {
            logger.debug("Cyclical depdency detected: ${nameVersionNode.name}")
            return null
        }
        cyclicalStack.push(nameVersionNode.name)

        NameVersionNode resolvedNode = nameVersionNode
        LinkMetadata metadata = getLinkMetadata(nameVersionNode)
        if (metadata?.linkNode) {
            resolvedNode = resolveLinks(cyclicalStack, metadata.linkNode)
        }

        if (resolvedNode) {
            List<NameVersionNode> resolvedChildren = []
            resolvedNode.children.each {
                NameVersionNode resolvedChild = resolveLinks(cyclicalStack, it)
                if (resolvedChild) {
                    resolvedChildren.add(resolvedChild)
                }
            }
            resolvedNode.children = resolvedChildren
        } else {
            resolvedNode = nameVersionNode
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
