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
package com.synopsys.integration.detectable.detectables.gradle.parsenew.transformers;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleReport;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleTreeNode;

//An example transform that uses queue and recursion to build the Gradle graph. Possibly more difficult to grok?
public class GradleReportRecursiveTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GradleReportRecursiveTransformer(final ExternalIdFactory externalIdFactory) {this.externalIdFactory = externalIdFactory;}

    public CodeLocation trasnform(GradleReport gradleReport) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (GradleConfiguration configuration : gradleReport.configurations) {
            LinkedList<GradleTreeNode> nodeQueue = new LinkedList<>();
            nodeQueue.addAll(configuration.children);

            while (!nodeQueue.isEmpty()) {
                GradleTreeNode next = nodeQueue.pop();
                if (next.getLevel() == 0) {
                    walkStack(graph, nodeQueue, next, null);
                } //otherwise ignore non-root nodes
            }
        }

        ExternalId projectId = externalIdFactory.createMavenExternalId(gradleReport.projectGroup, gradleReport.projectName, gradleReport.projectVersionName);
        return new CodeLocation.Builder(CodeLocationType.GRADLE, graph, projectId).build();
    }

    public void walkStack(MutableDependencyGraph graph, LinkedList<GradleTreeNode> nodeQueue, GradleTreeNode currentNode, Dependency currentParent) {
        if (currentNode.getNodeType() == GradleTreeNode.NodeType.GAV) {
            GradleGav gav = currentNode.getGav().get();
            ExternalId externalId = externalIdFactory.createMavenExternalId(gav.getName(), gav.getArtifact(), gav.getVersion());
            Dependency currentDependency = new Dependency(gav.getArtifact(), gav.getVersion(), externalId);

            if (currentParent == null) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParent(currentDependency, currentParent);
            }

            while (nodeQueue.peek() != null && nodeQueue.peek().getLevel() == currentNode.getLevel() + 1) {
                GradleTreeNode next = nodeQueue.pop();
                walkStack(graph, nodeQueue, next, currentDependency);
            }
        }
    }
}
