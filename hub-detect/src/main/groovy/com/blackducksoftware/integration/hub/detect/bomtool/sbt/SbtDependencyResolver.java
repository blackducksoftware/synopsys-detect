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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class SbtDependencyResolver {
    public ExternalIdFactory externalIdFactory;

    public SbtDependencyResolver(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public SbtDependencyModule resolveReport(final SbtReport report) {
        final ExternalId rootId = externalIdFactory.createMavenExternalId(report.getOrganisation(), report.getModule(), report.getRevision());
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        report.getDependencies().forEach(module -> {
            module.getRevisions().forEach(revision -> {
                final ExternalId id = externalIdFactory.createMavenExternalId(module.getOrganisation(), module.getName(), revision.getName());
                final Dependency child = new Dependency(module.getName(), revision.getName(), id);

                revision.getCallers().forEach(caller -> {
                    final ExternalId parentId = externalIdFactory.createMavenExternalId(caller.getOrganisation(), caller.getName(), caller.getRevision());
                    final Dependency parent = new Dependency(caller.getName(), caller.getRevision(), parentId);

                    if (rootId.equals(parentId)) {
                        graph.addChildToRoot(child);
                    } else {
                        graph.addParentWithChild(parent, child);
                    }
                });
            });
        });

        final SbtDependencyModule module = new SbtDependencyModule();
        module.name = report.getModule();
        module.version = report.getRevision();
        module.org = report.getOrganisation();

        module.graph = graph;
        module.configuration = report.getConfiguration();

        return module;
    }
}
