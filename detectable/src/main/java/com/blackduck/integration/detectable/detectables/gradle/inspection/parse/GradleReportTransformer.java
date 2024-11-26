package com.blackduck.integration.detectable.detectables.gradle.inspection.parse;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.util.DependencyHistory;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.gradle.inspection.GradleConfigurationType;
import com.blackduck.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;
import com.blackduck.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.blackduck.integration.detectable.detectables.gradle.inspection.model.GradleReport;
import com.blackduck.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

//An example transform that uses our "Dependency History" class and is closer to the original Gradle implementation
public class GradleReportTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EnumListFilter<GradleConfigurationType> configurationTypeFilter;
    private final List<CodeLocation> allCodeLocationsWithinRoot = new ArrayList<>();

    public GradleReportTransformer(EnumListFilter<GradleConfigurationType> configurationTypeFilter) {
        this.configurationTypeFilter = configurationTypeFilter;
    }

    public List<CodeLocation> transformRootReport(GradleReport gradleReport) {
        DependencyGraph rootGraph = new BasicDependencyGraph();
        for (GradleConfiguration configuration : gradleReport.getConfigurations()) {
            if (configuration.isResolved() || configurationTypeFilter.shouldInclude(GradleConfigurationType.UNRESOLVED)) {
                logger.debug("Adding configuration to the graph: {}", configuration.getName());
                addConfigurationToGraph_rootFlow(rootGraph, configuration, gradleReport);
            } else {
                logger.trace("Excluding unresolved configuration from the graph: {}", configuration.getName());
            }
        }

        ExternalId projectId = ExternalId.FACTORY.createMavenExternalId(gradleReport.getProjectGroup(), gradleReport.getProjectName(), gradleReport.getProjectVersionName());
        if (StringUtils.isNotBlank(gradleReport.getProjectSourcePath())) {
            CodeLocation rootCodeLocation = new CodeLocation(rootGraph, projectId, new File(gradleReport.getProjectSourcePath())); /// path for when the only report is root we have: /Users/shanty/blackduck/github-folder/detect
            allCodeLocationsWithinRoot.add(rootCodeLocation);
        } else {
            CodeLocation rootCodeLocation = new CodeLocation(rootGraph, projectId);
            allCodeLocationsWithinRoot.add(rootCodeLocation);
        }

        return allCodeLocationsWithinRoot;
    }


    private void addConfigurationToGraph_rootFlow(DependencyGraph rootGraph, GradleConfiguration configuration, GradleReport rootReport) {
        DependencyHistory history = new DependencyHistory();

        TreeNodeSkipper treeNodeSkipper = new TreeNodeSkipper();
        for (GradleTreeNode currentNode : configuration.getChildren()) {
            if (treeNodeSkipper.shouldSkip(currentNode)) {
                logger.debug("~~~Skipping node: " + currentNode.getGav());
                continue;
            }

            if (currentNode.getNodeType() == GradleTreeNode.NodeType.GAV) {
                history.clearDependenciesDeeperThan(currentNode.getLevel());
                Optional<GradleGav> currentNodeGav = currentNode.getGav();
                if (currentNodeGav.isPresent()) {
                    addGavToGraph(currentNodeGav.get(), history, rootGraph);
                }
                else {
                    // We know this is a GradleTreeNode.NodeType.GAV
                    // So if its missing data, something is probably wrong.
                    logger.debug("Missing expected GAV from known NodeType. {}", currentNode);
                }
            }
            else if (currentNode.getNodeType() == GradleTreeNode.NodeType.PROJECT) {
                // Let this method skip over the subProject section as usual
                treeNodeSkipper.skipUntilLineLevel(currentNode.getLevel());
                // Process subProject section separately
                processSubprojectAndCreateCodeLocation(currentNode, configuration.getChildren(), rootReport);
            } else {
                treeNodeSkipper.skipUntilLineLevel(currentNode.getLevel());
            }
        }
    }
    private void processSubprojectAndCreateCodeLocation(GradleTreeNode subProjectNode, List<GradleTreeNode> allTreeNodesInCurrentConfiguration, GradleReport rootReport) {
        logger.debug("Processing subProject node:" + subProjectNode.getProjectName());
        String subProjectName = subProjectNode.getProjectName().get();
        int subProjectSectionStartIndex = allTreeNodesInCurrentConfiguration.indexOf(subProjectNode);
        int subProjectNodeLevel = subProjectNode.getLevel();

        DependencyGraph subProjectGraph = new BasicDependencyGraph();
        DependencyHistory history = new DependencyHistory();
        TreeNodeSkipper treeNodeSkipper = new TreeNodeSkipper();


        for (int i = subProjectSectionStartIndex+1; i < allTreeNodesInCurrentConfiguration.size(); i++) {
            GradleTreeNode currentNode = allTreeNodesInCurrentConfiguration.get(i);
            int currentNodeLevelRelativeToSubProject = currentNode.getLevel() - 1;
            if (currentNodeLevelRelativeToSubProject != -1) { // (aka subProjectNodeLevel -1)
                if (currentNode.getNodeType() == GradleTreeNode.NodeType.GAV) {
                    logger.debug("Adding dependency " + currentNode.getGav() + " for subProject " + subProjectName);
                    history.clearDependenciesDeeperThan(currentNodeLevelRelativeToSubProject);
                    Optional<GradleGav> currentNodeGav = currentNode.getGav();
                    if (currentNodeGav.isPresent()) {
                        addGavToGraph(currentNodeGav.get(), history, subProjectGraph);
                    }
                    else {
                        // We know this is a GradleTreeNode.NodeType.GAV
                        // So if its missing data, something is probably wrong.
                        logger.debug("Missing expected GAV from known NodeType. {}", currentNode);
                    }
                } else {
                    // current node is either unknown or a nested subproject, todo later
                    logger.debug("Encountered unknown or nested project node while processing subProject");
                }
            } else {
                // the current node is back at 0 so we are done processing subProject section
                break;
            }
        }

        // have we encountered this subProject before?
        // if not, create new code location
        CodeLocation newCodeLocation = createCodeLocationForSubProject(rootReport, subProjectGraph, subProjectName);
        allCodeLocationsWithinRoot.add(newCodeLocation);
    }

    private CodeLocation createCodeLocationForSubProject(GradleReport rootReport, DependencyGraph subProjectGraph, String subProjectName) {
        ExternalId projectId = ExternalId.FACTORY.createMavenExternalId(rootReport.getProjectGroup(), subProjectName, rootReport.getProjectVersionName());
        if (StringUtils.isNotBlank(rootReport.getProjectSourcePath())) {
            return new CodeLocation(subProjectGraph, projectId, new File(rootReport.getProjectSourcePath() + "/" + subProjectName)); /// path for when the only report is root we have: /Users/shanty/blackduck/github-folder/detect
        } else {
            return new CodeLocation(subProjectGraph, projectId);
        }
    }

    public CodeLocation transform(GradleReport gradleReport) {
        DependencyGraph graph = new BasicDependencyGraph();

        for (GradleConfiguration configuration : gradleReport.getConfigurations()) {
            if (configuration.isResolved() || configurationTypeFilter.shouldInclude(GradleConfigurationType.UNRESOLVED)) {
                logger.trace("Adding configuration to the graph: {}", configuration.getName());
                addConfigurationToGraph(graph, configuration);
            } else {
                logger.trace("Excluding unresolved configuration from the graph: {}", configuration.getName());
            }
        }

        ExternalId projectId = ExternalId.FACTORY.createMavenExternalId(gradleReport.getProjectGroup(), gradleReport.getProjectName(), gradleReport.getProjectVersionName());
        if (StringUtils.isNotBlank(gradleReport.getProjectSourcePath())) {
            return new CodeLocation(graph, projectId, new File(gradleReport.getProjectSourcePath()));
        } else {
            return new CodeLocation(graph, projectId);
        }
    }

    private void addConfigurationToGraph(DependencyGraph graph, GradleConfiguration configuration) {
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

    private void addGavToGraph(GradleGav gav, DependencyHistory history, DependencyGraph graph) {
        Dependency currentDependency = Dependency.FACTORY.createMavenDependency(gav.getGroup(), gav.getName(), gav.getVersion());
        if (history.isEmpty()) {
            graph.addDirectDependency(currentDependency);
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
