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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt.parse

import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtDependencyModule
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.reports.model.SbtReport

import groovy.transform.TypeChecked

@TypeChecked
public class SbtDependencyResolver {

    public ExternalIdFactory externalIdFactory;
    public SbtDependencyResolver(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public SbtDependencyModule resolveReport(SbtReport report) {
        def rootId = externalIdFactory.createMavenExternalId(report.organisation, report.module, report.revision)
        List<SbtDependencyModule> modules = new ArrayList<>();

        MutableDependencyGraph graph = new MutableMapDependencyGraph()

        report.dependencies.each { module ->
            module.revisions.each { revision ->
                def id = externalIdFactory.createMavenExternalId(module.organisation, module.name, revision.name)
                def child = new Dependency(module.name, revision.name, id)

                revision.callers.each { caller ->
                    def parentId = externalIdFactory.createMavenExternalId(caller.callerOrganisation, caller.callerName, caller.callerRevision)
                    def parent = new Dependency(caller.callerName, caller.callerRevision, parentId)
                    if (rootId.equals(parentId)) {
                        graph.addChildToRoot(child);
                    } else {
                        graph.addParentWithChild(parent, child)
                    }
                }
            }
        }

        def module = new SbtDependencyModule()
        module.name = report.getModule()
        module.version = report.getRevision()
        module.org = report.getOrganisation()

        module.graph = graph;
        module.configuration = report.configuration

        module
    }
}
