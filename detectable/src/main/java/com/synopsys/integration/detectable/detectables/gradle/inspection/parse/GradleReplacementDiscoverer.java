/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

public class GradleReplacementDiscoverer {
    private final ExternalIdFactory externalIdFactory;

    public GradleReplacementDiscoverer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public void populateFromTreeNodes(DependencyReplacementResolver dependencyReplacementResolver, List<GradleTreeNode> gradleTreeNodes) {
        for (GradleTreeNode currentNode : gradleTreeNodes) {
            Optional<GradleGav> resolvedGavOptional = currentNode.getGav();
            Optional<GradleGav> replacedGavOptional = currentNode.getReplacedGav();
            if (currentNode.getNodeType() != GradleTreeNode.NodeType.GAV || !resolvedGavOptional.isPresent() || !replacedGavOptional.isPresent()) {
                continue;
            }
            GradleGav resolvedGav = resolvedGavOptional.get();
            GradleGav replacedGav = replacedGavOptional.get();

            ExternalId externalId = externalIdFactory.createMavenExternalId(resolvedGav.getGroup(), resolvedGav.getArtifact(), resolvedGav.getVersion());
            Dependency resolvedDependency = new Dependency(resolvedGav.getArtifact(), resolvedGav.getVersion(), externalId);

            ExternalId replacedExternalId = externalIdFactory.createMavenExternalId(replacedGav.getGroup(), replacedGav.getArtifact(), replacedGav.getVersion());
            Dependency replacedDependency = new Dependency(replacedGav.getArtifact(), replacedGav.getVersion(), replacedExternalId);

            dependencyReplacementResolver.addReplacementData(replacedDependency, resolvedDependency);
        }
    }
}
