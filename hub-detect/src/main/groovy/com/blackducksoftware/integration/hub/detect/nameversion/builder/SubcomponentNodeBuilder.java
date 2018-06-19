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
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.SubcomponentMetadata;

/**
 * The use for this is because the KB currently does not support sub components in cocoapods. Until such time we must collapse sub commponents into their super component
 *
 * Takes the children of a NameVersionNode and moves them to the super component. Subcomponents should have a link to the superComponent in the metadata. The superComponents should have all subcomponents in their metadata. All
 * superComponents should be added the the superComponents list.
 */
public class SubcomponentNodeBuilder extends LinkedNameVersionNodeBuilder {
    private final List<NameVersionNode> superComponents = new ArrayList<>();

    public SubcomponentNodeBuilder(final NameVersionNode root) {
        super(root);
    }

    @Override
    public NameVersionNode build() {
        for (final NameVersionNode superComponent : superComponents) {
            collapseSubcomponents(superComponent);
        }

        return super.build();
    }

    public NameVersionNode collapseSubcomponents(final NameVersionNode nameVersionNode) {
        if (nameVersionNode != null && nameVersionNode.getMetadata() instanceof SubcomponentMetadata) {
            final SubcomponentMetadata metadata = (SubcomponentMetadata) nameVersionNode.getMetadata();
            for (final NameVersionNode subcomponent : metadata.getSubcomponents()) {
                final String subcomponentVersion = StringUtils.trimToEmpty(subcomponent.getVersion());
                if (StringUtils.isBlank(nameVersionNode.getVersion()) && StringUtils.isNotBlank(subcomponentVersion)) {
                    nameVersionNode.setVersion(subcomponentVersion);
                }
                for (final NameVersionNode subcomponentChild : subcomponent.getChildren()) {
                    nameVersionNode.getChildren().add(collapseSubcomponents(subcomponentChild));
                }
            }
        }

        return nameVersionNode;
    }

    public List<NameVersionNode> getSuperComponents() {
        return superComponents;
    }
}
