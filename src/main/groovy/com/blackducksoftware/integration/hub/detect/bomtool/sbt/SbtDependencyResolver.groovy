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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtConfigurationDependencyTree
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtReport

import groovy.transform.TypeChecked

@TypeChecked
public class SbtDependencyResolver {
    public SbtConfigurationDependencyTree resolveReportDependencies(SbtReport report) {
        def rootId = new MavenExternalId(report.organisation, report.module, report.revision)
        def root = new DependencyNode(report.module, report.revision, rootId )

        def builder = new DependencyNodeBuilder(root)

        report.dependencies.each { module ->
            module.revisions.each { revision ->
                def id = new MavenExternalId(module.organisation, module.name, revision.name)
                def node = new DependencyNode(module.name, revision.name, id)

                List<DependencyNode> children = new ArrayList<DependencyNode>()
                revision.callers.each { caller ->
                    def childId = new MavenExternalId(caller.callerOrganisation, caller.callerName, caller.callerRevision)
                    def childNode = new DependencyNode(caller.callerName, caller.callerRevision, childId)
                    children.add(childNode)
                }

                builder.addChildNodeWithParents(node, children)
            }
        }

        def config = new SbtConfigurationDependencyTree()
        config.rootNode = root
        config.configuration = report.configuration
        config
    }
}
