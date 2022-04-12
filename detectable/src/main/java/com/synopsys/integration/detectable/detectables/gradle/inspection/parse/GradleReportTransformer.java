package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.ProjectDependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.DependencyHistory;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleConfigurationType;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleReport;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

//An example transform that uses our "Dependency History" class and is closer to the original Gradle implementation
public class GradleReportTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;
    private final EnumListFilter<GradleConfigurationType> configurationTypeFilter;

    public GradleReportTransformer(ExternalIdFactory externalIdFactory, EnumListFilter<GradleConfigurationType> configurationTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.configurationTypeFilter = configurationTypeFilter;
    }

    public CodeLocation transform(GradleReport gradleReport) {
        ProjectDependency projectDependency = new ProjectDependency(Dependency.FACTORY.createMavenDependency(
            gradleReport.getProjectGroup(),
            gradleReport.getProjectName(),
            gradleReport.getProjectVersionName()
        ));
        // isRootProjectPlaceholder = true for now. I think eventually (8.0.0) we could change how the graph is built to use this node instead.
        MutableDependencyGraph graph = new MutableMapDependencyGraph(projectDependency, true);

        for (GradleConfiguration configuration : gradleReport.getConfigurations()) {
            if (configuration.isResolved() || configurationTypeFilter.shouldInclude(GradleConfigurationType.UNRESOLVED)) {
                logger.trace("Adding configuration to the graph: {}", configuration.getName());
                addConfigurationToGraph(graph, configuration);
            } else {
                logger.trace("Excluding unresolved configuration from the graph: {}", configuration.getName());
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

        TreeNodeSkipper treeNodeSkipper = new TreeNodeSkipper();
        for (GradleTreeNode currentNode : configuration.getChildren()) {
            if (treeNodeSkipper.shouldSkip(currentNode)) {
                continue;
            }

            if (currentNode.getNodeType() == GradleTreeNode.NodeType.GAV) {
                history.clearDependenciesDeeperThan(currentNode.getLevel());
                Optional<GradleGav> currentNodeGav = currentNode.getGav();
                if (currentNodeGav.isPresent()) {
                    addGavToGraph(currentNodeGav.get(), history, graph);
                } else {
                    // We know this is a GradleTreeNode.NodeType.GAV
                    // So if its missing data, something is probably wrong.
                    logger.debug("Missing expected GAV from known NodeType. {}", currentNode);
                }
            } else {
                treeNodeSkipper.skipUntilLineLevel(currentNode.getLevel());
            }
        }
    }

    private void addGavToGraph(GradleGav gav, DependencyHistory history, MutableDependencyGraph graph) {
        ExternalId externalId = externalIdFactory.createMavenExternalId(gav.getGroup(), gav.getName(), gav.getVersion());
        Dependency currentDependency = new Dependency(gav.getName(), gav.getVersion(), externalId);

        if (history.isEmpty()) {
            graph.addChildToRoot(currentDependency);
        } else {
            graph.addChildWithParents(currentDependency, history.getLastDependency());
        }
        history.add(currentDependency);
    }

    private static class TreeNodeSkipper {
        private Optional<Integer> skipUntilLineLevel = Optional.empty();

        public boolean shouldSkip(GradleTreeNode nodeInQuestion) {
            if (skipUntilLineLevel.isPresent()) {
                if (nodeInQuestion.getLevel() > skipUntilLineLevel.get()) {
                    return true;
                } else {
                    skipUntilLineLevel = Optional.empty();
                    return false;
                }
            } else {
                return false;
            }
        }

        public void skipUntilLineLevel(int lineLevel) {
            skipUntilLineLevel = Optional.of(lineLevel);
        }
    }

}
