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

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.SubcomponentMetadata

import groovy.transform.TypeChecked

/**
 * The use for this is because the KB currently does not support sub components in
 * cocoapods. Until such time we must collapse sub commponents into their super component
 *
 * Takes the children of a NameVersionNode and moves them to the super component.
 * Subcomponents should have a link to the superComponent in the metadata.
 * The superComponents should have all subcomponents in their metadata.
 * All superComponents should be added the the superComponents list.
 */
@TypeChecked
class SubcomponentNodeBuilder extends LinkedNameVersionNodeBuilder {
    List<NameVersionNode> superComponents = []

    public SubcomponentNodeBuilder(NameVersionNode root) {
        super(root)
    }

    @Override
    public NameVersionNode build() {
        superComponents.each { collapseSubcomponents(it) }

        super.build()
    }

    public NameVersionNode collapseSubcomponents(NameVersionNode nameVersionNode) {
        if (nameVersionNode?.metadata instanceof SubcomponentMetadata) {
            SubcomponentMetadata metadata = nameVersionNode.metadata as SubcomponentMetadata
            String name = nameVersionNode.name
            String version = !nameVersionNode.version?.trim()
            metadata.subcomponents.each { subcomponent ->
                String subcomponentVersion = subcomponent.version?.trim()
                if (version && subcomponentVersion) {
                    nameVersionNode.version = subcomponentVersion
                }
                nameVersionNode.children.addAll(subcomponent.children.collect { collapseSubcomponents(it as NameVersionNode) })
            }
        }

        nameVersionNode
    }
}
