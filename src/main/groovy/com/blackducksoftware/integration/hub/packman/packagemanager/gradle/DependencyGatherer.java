package com.blackducksoftware.integration.hub.packman.packagemanager.gradle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.buildtool.Gav;

public class DependencyGatherer {
    final Map<String, DependencyNode> visitedMap = new HashMap<>();

    final String includedConfigurations;

    final Set<String> excludedModules = new HashSet<>();

    public DependencyGatherer(final String includedConfigurations, final String excludedModules) {
        this.includedConfigurations = includedConfigurations;
        if (StringUtils.isNotBlank(excludedModules)) {
            final String[] pieces = excludedModules.split(",");
            for (final String piece : pieces) {
                if (StringUtils.isNotBlank(piece)) {
                    this.excludedModules.add(piece);
                }
            }
        }
    }

    public DependencyNode getFullyPopulatedRootNode(final Project project, final String hubProjectName, final String hubProjectVersion) {
        logger.info("creating the dependency graph");
        final String groupId = project.getGroup().toString();
        final String artifactId = project.getName();
        final String version = hubProjectVersion;
        final Gav projectGav = new Gav(groupId, artifactId, version);

        final List<DependencyNode> children = new ArrayList<>();
        final DependencyNode root = new DependencyNode(projectGav, children);
        for (final Project childProject : project.getAllprojects()) {
            if (!excludedModules.contains(childProject.getName())) {
                getProjectDependencies(childProject, children);
            }
        }

        return root;
    }

    private void getProjectDependencies(final Project project, final List<DependencyNode> children) {
        final ScopesHelper scopesHelper = new ScopesHelper(project, includedConfigurations);
        final Set<Configuration> configurations = project.getConfigurations();
        for (final Configuration configuration : configurations) {
            final String configName = configuration.getName();
            if (scopesHelper.shouldIncludeConfigurationInDependencyGraph(configName)) {
                logger.debug("Resolving dependencies for project: " + project.getName());
                final ResolvedConfiguration resolvedConfiguration = configuration.getResolvedConfiguration();
                final Set<ResolvedDependency> resolvedDependencies = resolvedConfiguration
                        .getFirstLevelModuleDependencies();
                for (final ResolvedDependency resolvedDependency : resolvedDependencies) {
                    children.add(createCommonDependencyNode(resolvedDependency, 0, configName));
                }
            }
        }
    }

    private DependencyNode createCommonDependencyNode(final ResolvedDependency resolvedDependency, final int level,
            final String configuration) {
        final Gav gav = createGavFromDependencyNode(resolvedDependency);
        final String gavKey = gav.toString();

        final StringBuffer sb = new StringBuffer();
        if (logger.isDebugEnabled()) {
            sb.append("|");
            for (int i = 0; i < level; i++) {
                sb.append(" ");
            }
            sb.append("(");
            sb.append(level);
            sb.append(")-> ");
        }
        final String buffer = sb.toString();
        if (visitedMap.containsKey(gavKey)) {
            if (logger.isDebugEnabled()) {
                logger.debug(buffer + gavKey + " (already visited) config: " + configuration);
            }
            return visitedMap.get(gavKey);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(buffer + gavKey + " (created) config: " + configuration);
            }
            final List<DependencyNode> children = new ArrayList<>();
            final DependencyNode dependencyNode = new DependencyNode(gav, children);
            for (final ResolvedDependency child : resolvedDependency.getChildren()) {
                if (child.getConfiguration().equals(configuration)) {
                    children.add(createCommonDependencyNode(child, level + 1, configuration));
                }
            }
            visitedMap.put(gavKey, dependencyNode);
            return dependencyNode;
        }
    }

    private Gav createGavFromDependencyNode(final ResolvedDependency resolvedDependency) {
        final String groupId = resolvedDependency.getModuleGroup();
        final String artifactId = resolvedDependency.getModuleName();
        final String version = resolvedDependency.getModuleVersion();

        final Gav gav = new Gav(groupId, artifactId, version);
        return gav;
    }

}
