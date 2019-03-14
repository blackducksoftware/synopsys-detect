/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public YarnTransformer(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(List<YarnListNode> yarnList, YarnLock yarnLock){
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final DependencyHistory history = new DependencyHistory();

        for (YarnListNode yarnListNode : yarnList){
            int actualDepth = yarnListNode.getDepth() - 1;//"yarn list" is the root node, so we need to shift the entire tree 1 level to the left.
            try {
                history.clearDependenciesDeeperThan(actualDepth);
            } catch (final IllegalStateException e) {
                logger.warn(String.format("Problem parsing yarn list '%s': %s", yarnListNode.getFuzzyId(), e.getMessage()));
            }

            String name = yarnListNode.getPackageName();
            Optional<String> resolvedVersion = yarnLock.versionForFuzzyId(yarnListNode.getFuzzyId());
            String version = resolvedVersion.orElse(yarnListNode.getFuzzyPackageVersion());

            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
            Dependency dependency = new Dependency(name, version, externalId);


            if (history.isEmpty()) {
                graph.addChildToRoot(dependency);
            } else {
                graph.addChildWithParents(dependency, history.getLastDependency());
            }

            history.add(dependency);
        }

        return graph;
    }
}
