/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyBuilderMissingExternalIdHandler;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.StringDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspace;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.NameVersion;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public YarnTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(YarnLockResult yarnLockResult, boolean productionOnly, boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        List<NameVersion> externalDependencies, @Nullable ExcludedIncludedWildcardFilter workspaceFilter) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        addRootNodesToGraph(graphBuilder, yarnLockResult.getRootPackageJson(), yarnLockResult.getWorkspaceData(), productionOnly,
            getWorkspaceDependenciesFromWorkspacePackageJson, workspaceFilter);

        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            if (entry.getIds().get(0).getName().contains("workspace")) {
                System.out.println("Found a workspace");
            }
            if (entry.getIds().get(0).getName().contains("semver")) {
                System.out.println("Found semver");
            }
            Optional<YarnWorkspace> workspace = yarnLockResult.getWorkspaceData().lookup(entry);
            if (workspace.isPresent()) {
                StringDependencyId id = workspace.get().generateDependencyId();
                ExternalId externalId = workspace.get().generateExternalId();
                graphBuilder.setDependencyInfo(id, workspace.get().getWorkspacePackageJson().getPackageJson().name, workspace.get().getWorkspacePackageJson().getPackageJson().version, externalId);
                // TODO this is duplicate of code below:
                for (YarnLockDependency dependency : entry.getDependencies()) {
                    // TODO this feels repetetive
                    StringDependencyId stringDependencyId;
                    Optional<YarnWorkspace> dependencyWorkspace = yarnLockResult.getWorkspaceData().lookup(dependency);
                    if (dependencyWorkspace.isPresent()) {
                        stringDependencyId = dependencyWorkspace.get().generateDependencyId();
                    } else {
                        stringDependencyId = new StringDependencyId(dependency.getName() + "@" + dependency.getVersion());
                    }
                    if (!productionOnly || !dependency.isOptional()) {
                        graphBuilder.addChildWithParent(stringDependencyId, id);
                    } else {
                        logger.debug("Excluding optional dependency: {}", stringDependencyId.getValue());
                    }
                }
                ////////////////
            } else {
                for (YarnLockEntryId entryId : entry.getIds()) {
                    StringDependencyId id = new StringDependencyId(entryId.getName() + "@" + entryId.getVersion());
                    graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), externalIdFactory.createNameVersionExternalId(Forge.NPMJS, entryId.getName(), entry.getVersion()));
                    for (YarnLockDependency dependency : entry.getDependencies()) {
                        // TODO This code appears lots of places now
                        StringDependencyId stringDependencyId;
                        Optional<YarnWorkspace> dependencyWorkspace = yarnLockResult.getWorkspaceData().lookup(dependency);
                        if (dependencyWorkspace.isPresent()) {
                            stringDependencyId = dependencyWorkspace.get().generateDependencyId();
                        } else {
                            stringDependencyId = new StringDependencyId(dependency.getName() + "@" + dependency.getVersion());
                        }
                        if (!productionOnly || !dependency.isOptional()) {
                            graphBuilder.addChildWithParent(stringDependencyId, id);
                        } else {
                            logger.debug("Excluding optional dependency: {}", stringDependencyId.getValue());
                        }
                    }
                }
            }
        }
        return graphBuilder.build(getLazyBuilderHandler(externalDependencies, yarnLockResult));
    }

    private LazyBuilderMissingExternalIdHandler getLazyBuilderHandler(List<NameVersion> externalDependencies, YarnLockResult yarnLockResult) {
        return (dependencyId, lazyDependencyInfo) -> {
            Optional<NameVersion> externalDependency = externalDependencies.stream().filter(it -> it.getName().equals(lazyDependencyInfo.getName())).findFirst();
            Optional<ExternalId> externalId = externalDependency.map(it -> externalIdFactory.createNameVersionExternalId(Forge.NPMJS, it.getName(), it.getVersion()));

            if (externalId.isPresent()) {
                return externalId.get();
            } else {
                StringDependencyId stringDependencyId = (StringDependencyId) dependencyId;
                Optional<YarnWorkspace> workspace = yarnLockResult.getWorkspaceData().lookup(stringDependencyId);
                if (workspace.isPresent()) {
                    logger.warn("Workspace {} wasn't define when the graph was built; adding it during build step", stringDependencyId.getValue());
                    return workspace.get().generateExternalId();
                } else {
                    logger.warn("Missing yarn dependency. '{}' is neither a defined workspace nor a dependency defined in {}.", stringDependencyId.getValue(), yarnLockResult.getYarnLockFilePath());
                }
                return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, stringDependencyId.getValue());
            }
        };
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        PackageJson rootPackageJson, YarnWorkspaces workspaceData, boolean productionOnly,
        boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        @Nullable ExcludedIncludedWildcardFilter workspacesFilter) {
        logger.debug("Adding root dependencies from root PackageJson: {}:{}", rootPackageJson.name, rootPackageJson.version);
        if ((workspacesFilter == null) || workspacesFilter.willInclude(rootPackageJson.name)) {
            populateGraphWithRootDependencies(graphBuilder, rootPackageJson, productionOnly, workspaceData);
        }
        if ((workspacesFilter != null) || getWorkspaceDependenciesFromWorkspacePackageJson) {
            populateGraphFromWorkspaceData(graphBuilder, workspaceData, productionOnly, getWorkspaceDependenciesFromWorkspacePackageJson, workspacesFilter);
        }
    }

    private void populateGraphWithRootDependencies(LazyExternalIdDependencyGraphBuilder graphBuilder, PackageJson rootPackageJson, boolean productionOnly, YarnWorkspaces workspaceData) {
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.dependencies, workspaceData);
        if (!productionOnly) {
            logger.debug("\tAlso adding dev dependencies");
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.devDependencies, workspaceData);
        }
    }

    private void populateGraphFromWorkspaceData(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspaces workspaceData, boolean productionOnly,
        boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        @Nullable ExcludedIncludedWildcardFilter workspacesFilter) {
        for (YarnWorkspace curWorkspace : workspaceData.getWorkspaces()) {
            StringDependencyId workspaceId = curWorkspace.generateDependencyId();
            if ((workspacesFilter != null) && workspacesFilter.willInclude(curWorkspace.getWorkspacePackageJson().getPackageJson().name)) {
                logger.debug("Adding root dependency representing workspace included by filter from workspace PackageJson: {}:{} ({})", curWorkspace.getWorkspacePackageJson().getPackageJson().name,
                    curWorkspace.getWorkspacePackageJson().getPackageJson().version,
                    workspaceId);
                graphBuilder.addChildToRoot(workspaceId);
                ExternalId externalId = curWorkspace.generateExternalId();
                graphBuilder.setDependencyInfo(workspaceId, curWorkspace.getWorkspacePackageJson().getPackageJson().name, curWorkspace.getWorkspacePackageJson().getPackageJson().version, externalId);
            }
            if (getWorkspaceDependenciesFromWorkspacePackageJson) {
                addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getWorkspacePackageJson().getPackageJson().dependencies.entrySet());
                if (!productionOnly) {
                    addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getWorkspacePackageJson().getPackageJson().devDependencies.entrySet());
                }
            }
        }
    }

    private void addWorkspaceChildrenToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspaces workspaceData, StringDependencyId workspaceId, Set<Map.Entry<String, String>> workspaceDependenciesToAdd) {
        for (Map.Entry<String, String> depOfWorkspace : workspaceDependenciesToAdd) {
            // TODO this is feeling redundant
            StringDependencyId depOfWorkspaceId;
            Optional<YarnWorkspace> workspace = workspaceData.lookup(depOfWorkspace.getKey(), depOfWorkspace.getValue());
            if (workspace.isPresent()) {
                depOfWorkspaceId = workspace.get().generateDependencyId();
                // TODO boy this feels like the 10th place I've done this...
                ExternalId externalId = workspace.get().generateExternalId();
                graphBuilder.setDependencyInfo(depOfWorkspaceId, workspace.get().getWorkspacePackageJson().getPackageJson().name, workspace.get().getWorkspacePackageJson().getPackageJson().version, externalId);
            } else { // TODO this should be a method; it's repeated a lot
                depOfWorkspaceId = new StringDependencyId(depOfWorkspace.getKey() + "@" + depOfWorkspace.getValue());
            }
            logger.debug("Adding dependency of workspace ({}) as child of workspace {}", depOfWorkspaceId, workspaceId);
            graphBuilder.addChildWithParent(depOfWorkspaceId, workspaceId);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, YarnWorkspaces workspaceData) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd.entrySet()) {
            StringDependencyId stringDependencyId;
            /// TODO this code is duplicated
            Optional<YarnWorkspace> workspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
            if (workspace.isPresent()) {
                stringDependencyId = workspace.get().generateDependencyId();
                ExternalId externalId = workspace.get().generateExternalId();
                graphBuilder.setDependencyInfo(stringDependencyId, workspace.get().getWorkspacePackageJson().getPackageJson().name, workspace.get().getWorkspacePackageJson().getPackageJson().version, externalId);
            } else {
                stringDependencyId = new StringDependencyId(rootDependency.getKey() + "@" + rootDependency.getValue());
            }
            logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
            graphBuilder.addChildToRoot(stringDependencyId);
        }
    }
}
