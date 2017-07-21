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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtConfigurationDependencyTree
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtReport

public class SbtDependencyResolver {
    public SbtConfigurationDependencyTree resolveReportDependencies(SbtReport report) {
        def rootId = new MavenExternalId(report.organisation, report.module, report.revision);
        def root = new DependencyNode(report.module, report.revision, rootId );

        def builder = new DependencyNodeBuilder(root);

        report.dependencies.each { module ->
            module.revisions.each {revision ->
                def id = new MavenExternalId(module.organisation, module.name, revision.name);
                def node = new DependencyNode(module.name, revision.name, id)

                List<DependencyNode> children = new ArrayList<DependencyNode>();
                revision.callers.each {caller ->
                    def childId = new MavenExternalId(caller.callerOrganisation, caller.callerName, caller.callerRevision);
                    def childNode = new DependencyNode(caller.callerName, caller.callerRevision, childId)
                    children.add(childNode)
                }

                builder.addChildNodeWithParents(node, children)
            }
        }

        def config = new SbtConfigurationDependencyTree();
        config.rootNode = root;
        config.configuration = report.configuration;
        config
    }
}
