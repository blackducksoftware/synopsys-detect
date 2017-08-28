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

    @Override
    public NameVersionNode build() {
        resolveLinks(root)
    }

    private NameVersionNode resolveLinks(NameVersionNode nameVersionNode) {
        NameVersionNode resolvedNode = nameVersionNode

        if (nameVersionNode.metadata && nameVersionNode.metadata instanceof LinkMetadata) {
            LinkMetadata metadata = (LinkMetadata) nameVersionNode.metadata
            if (metadata.linkNode) {
                resolvedNode = resolveLinks(metadata.linkNode)
            }
        }

        resolvedNode
    }
}
