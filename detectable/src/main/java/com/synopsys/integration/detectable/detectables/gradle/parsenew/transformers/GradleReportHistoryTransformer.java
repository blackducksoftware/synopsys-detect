package com.synopsys.integration.detectable.detectables.gradle.parsenew.transformers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleReport;
import com.synopsys.integration.detectable.detectables.gradle.model.GradleTreeNode;

//An example transformer that uses our "Dependency History" class and is closer to the original Gradle implementation
public class GradleReportHistoryTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GradleReportHistoryTransformer(final ExternalIdFactory externalIdFactory) {this.externalIdFactory = externalIdFactory;}

    public CodeLocation trasnform(GradleReport gradleReport) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for (GradleConfiguration configuration : gradleReport.configurations) {
            addConfigurationToGraph(graph, configuration);
        }

        ExternalId projectId = externalIdFactory.createMavenExternalId(gradleReport.projectGroup, gradleReport.projectName, gradleReport.projectVersionName);
        return new CodeLocation.Builder(CodeLocationType.GRADLE, graph, projectId).build();
    }

    public void addConfigurationToGraph(MutableDependencyGraph graph, GradleConfiguration configuration) {
        DependencyHistory history = new DependencyHistory();
        for (GradleTreeNode currentNode : configuration.children) {
            history.clearDependenciesDeeperThan(currentNode.getLevel());
            if (currentNode.getNodeType() != GradleTreeNode.NodeType.GAV) {
                continue;
            }

            GradleGav gav = currentNode.getGav().get();
            ExternalId externalId = externalIdFactory.createMavenExternalId(gav.getName(), gav.getArtifact(), gav.getVersion());
            Dependency currentDependency = new Dependency(gav.getArtifact(), gav.getVersion(), externalId);

            if (history.isEmpty()) {
                graph.addChildToRoot(currentDependency);
            } else {
                graph.addChildWithParents(currentDependency, history.getLastDependency());
            }
            history.add(currentDependency);
        }
    }
}
