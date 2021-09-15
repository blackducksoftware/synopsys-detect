/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleReport;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

//An example transform that uses our "Dependency History" class and is closer to the original Gradle implementation
public class GradleReportTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final boolean includeUnresolvedConfigurations;

    public GradleReportTransformer(ExternalIdFactory externalIdFactory, boolean includeUnresolvedConfigurations) {
        this.externalIdFactory = externalIdFactory;
        this.includeUnresolvedConfigurations = includeUnresolvedConfigurations;
    }

    public CodeLocation transform(GradleReport gradleReport) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (GradleConfiguration configuration : gradleReport.getConfigurations()) {
            if (configuration.isUnresolved() && !includeUnresolvedConfigurations) {
                logger.trace("Skipping adding unresolved configuration to the graph: {}", configuration.getName());
            } else {
                logger.trace("Adding configuration to the graph: {}", configuration.getName());
                addConfigurationToGraph(graph, configuration);
            }
        }

        ExternalId projectId = externalIdFactory.createMavenExternalId(gradleReport.getProjectGroup(), gradleReport.getProjectName(), gradleReport.getProjectVersionName());
        if (StringUtils.isNotBlank(gradleReport.getProjectSourcePath())) {
            return new CodeLocation(graph, projectId, new File(gradleReport.getProjectSourcePath()));
        } else {
            return new CodeLocation(graph, projectId);
        }
    }

    private void addConfigurationToGraph(MutableDependencyGraph graph, GradleConfiguration configuration) {
        DependencyHistory history = new DependencyHistory();
        Optional<Integer> skipUntil = Optional.empty();

        for (GradleTreeNode currentNode : configuration.getChildren()) {

            if (skipUntil.isPresent() && currentNode.getLevel() <= skipUntil.get()) {
                skipUntil = Optional.empty();
            } else if (skipUntil.isPresent()) {
                continue;
            }

            history.clearDependenciesDeeperThan(currentNode.getLevel());
            if (currentNode.getNodeType() != GradleTreeNode.NodeType.GAV) {
                skipUntil = Optional.of(currentNode.getLevel());
                continue;
            }

            GradleGav gav = currentNode.getGav().get(); // TODO: Why are we not doing an isPresent() check here?
            ExternalId externalId = externalIdFactory.createMavenExternalId(gav.getName(), gav.getGroup(), gav.getVersion());
            Dependency currentDependency = new Dependency(gav.getGroup(), gav.getVersion(), externalId);

            if (history.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, history.getLastDependency());
            }
            history.add(currentDependency);
        }

    }
}
