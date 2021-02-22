/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.sbt.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtDependencyModule;
import com.synopsys.integration.detectable.detectables.sbt.parse.model.SbtReport;

public class SbtDependencyResolver {
    private final Logger logger = LoggerFactory.getLogger(SbtDependencyResolver.class);
    private final ExternalIdFactory externalIdFactory;

    public SbtDependencyResolver(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public SbtDependencyModule resolveReport(final SbtReport report) {
        final ExternalId rootId = externalIdFactory.createMavenExternalId(report.getOrganisation(), report.getModule(), report.getRevision());
        logger.debug("Created external id: " + rootId.toString());
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        logger.debug("Dependencies found: " + report.getDependencies().size());

        report.getDependencies().forEach(module -> {
            logger.debug("Revisions found: " + module.getRevisions().size());
            module.getRevisions().forEach(revision -> {
                logger.debug("Callers found: " + revision.getCallers().size());
                final ExternalId id = externalIdFactory.createMavenExternalId(module.getOrganisation(), module.getName(), revision.getName());
                final Dependency child = new Dependency(module.getName(), revision.getName(), id);

                revision.getCallers().forEach(caller -> {
                    final ExternalId parentId = externalIdFactory.createMavenExternalId(caller.getOrganisation(), caller.getName(), caller.getRevision());
                    final Dependency parent = new Dependency(caller.getName(), caller.getRevision(), parentId);
                    logger.debug("Caller id: " + parentId.toString());

                    if (rootId.equals(parentId)) {
                        graph.addChildToRoot(child);
                    } else {
                        graph.addParentWithChild(parent, child);
                    }
                });
            });
        });

        final SbtDependencyModule module = new SbtDependencyModule();
        module.setName(report.getModule());
        module.setVersion(report.getRevision());
        module.setOrg(report.getOrganisation());

        module.setGraph(graph);
        module.setConfiguration(report.getConfiguration());

        return module;
    }
}
