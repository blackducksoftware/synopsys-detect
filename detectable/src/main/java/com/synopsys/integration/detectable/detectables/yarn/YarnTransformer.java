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
import com.synopsys.integration.detectable.detectables.yarn.workspace.Workspace;
import com.synopsys.integration.detectable.detectables.yarn.workspace.WorkspaceData;
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
            Optional<Workspace> workspace = yarnLockResult.getWorkspaceData().lookup(entry);
            if (workspace.isPresent()) {
                StringDependencyId id = workspace.get().generateDependencyId();
                ExternalId externalId = workspace.get().generateExternalId();
                graphBuilder.setDependencyInfo(id, workspace.get().getWorkspacePackageJson().getPackageJson().name, workspace.get().getWorkspacePackageJson().getPackageJson().version, externalId);
                // TODO this is duplicate of code below:
                for (YarnLockDependency dependency : entry.getDependencies()) {
                    // TODO what if this is a workspace??
                    // TODO this feels repetetive
                    StringDependencyId stringDependencyId;
                    Optional<Workspace> dependencyWorkspace = yarnLockResult.getWorkspaceData().lookup(dependency);
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
                    ////////// TODO this may be what's wrong kkkk; kkk deps (below) might be workspaces too!!
                    graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), externalIdFactory.createNameVersionExternalId(Forge.NPMJS, entryId.getName(), entry.getVersion()));
                    for (YarnLockDependency dependency : entry.getDependencies()) {
                        // TODO what if this is a workspace??
                        StringDependencyId stringDependencyId = new StringDependencyId(dependency.getName() + "@" + dependency.getVersion());
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
                if (yarnLockResult.getWorkspaceData().isWorkspace(stringDependencyId)) {
                    logger.debug("Including workspace {} in the graph", stringDependencyId.getValue());
                } else {
                    logger.warn("Missing yarn dependency. '{}' is neither a defined workspace nor a dependency defined in {}.", stringDependencyId.getValue(), yarnLockResult.getYarnLockFilePath());
                }
                return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, stringDependencyId.getValue());
            }
        };
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        PackageJson rootPackageJson, WorkspaceData workspaceData, boolean productionOnly,
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

    private void populateGraphWithRootDependencies(LazyExternalIdDependencyGraphBuilder graphBuilder, PackageJson rootPackageJson, boolean productionOnly, WorkspaceData workspaceData) {
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.dependencies, workspaceData);
        if (!productionOnly) {
            logger.debug("\tAlso adding dev dependencies");
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.devDependencies, workspaceData);
        }
    }

    private void populateGraphFromWorkspaceData(LazyExternalIdDependencyGraphBuilder graphBuilder, WorkspaceData workspaceData, boolean productionOnly,
        boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        @Nullable ExcludedIncludedWildcardFilter workspacesFilter) {
        for (Workspace curWorkspace : workspaceData.getWorkspaces()) {
            // TODO figure out which is right:
            //StringDependencyId workspaceId = new StringDependencyId(curWorkspacePackageJson.name + "@" + curWorkspacePackageJson.version);
            StringDependencyId workspaceId = new StringDependencyId(curWorkspace.getWorkspacePackageJson().getPackageJson().name + "@workspace:" + curWorkspace.getWorkspacePackageJson().getDirRelativePath());

            if ((workspacesFilter != null) && workspacesFilter.willInclude(curWorkspace.getWorkspacePackageJson().getPackageJson().name)) {
                logger.debug("Adding root dependency representing workspace included by filter from workspace PackageJson: {}:{} ({})", curWorkspace.getWorkspacePackageJson().getPackageJson().name,
                    curWorkspace.getWorkspacePackageJson().getPackageJson().version,
                    workspaceId);
                graphBuilder.addChildToRoot(workspaceId);
            }
            if (getWorkspaceDependenciesFromWorkspacePackageJson) {
                addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getWorkspacePackageJson().getPackageJson().dependencies.entrySet());
                if (!productionOnly) {
                    addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getWorkspacePackageJson().getPackageJson().devDependencies.entrySet());
                }
            }
        }
    }

    private void addWorkspaceChildrenToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, WorkspaceData workspaceData, StringDependencyId workspaceId, Set<Map.Entry<String, String>> workspaceDependenciesToAdd) {
        for (Map.Entry<String, String> depOfWorkspace : workspaceDependenciesToAdd) {
            // TODO this is feeling redundant
            StringDependencyId depOfWorkspaceId;
            Optional<Workspace> workspace = workspaceData.lookup(depOfWorkspace.getKey(), depOfWorkspace.getValue());
            if (workspace.isPresent()) {
                depOfWorkspaceId = workspace.get().generateDependencyId();
            } else {
                depOfWorkspaceId = new StringDependencyId(depOfWorkspace.getKey() + "@" + depOfWorkspace.getValue());
            }
            logger.debug("Adding dependency of workspace ({}) as child of workspace {}", depOfWorkspaceId, workspaceId);
            graphBuilder.addChildWithParent(depOfWorkspaceId, workspaceId);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, WorkspaceData workspaceData) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd.entrySet()) {
            StringDependencyId stringDependencyId;
            /// TODO this code is duplicated
            Optional<Workspace> workspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
            if (workspace.isPresent()) {
                stringDependencyId = workspace.get().generateDependencyId();
                // TODO remove: stringDependencyId = new StringDependencyId(rootDependency.getKey() + "@workspace:" + workspace.get().getWorkspacePackageJson().getDirRelativePath());
            } else {
                stringDependencyId = new StringDependencyId(rootDependency.getKey() + "@" + rootDependency.getValue());
            }
            logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
            graphBuilder.addChildToRoot(stringDependencyId);
        }
    }

    //    @Nullable
    //    private WorkspacePackageJson lookupWorkspaceOLD(Map<String, WorkspacePackageJson> workspacePackageJsons, String depName, String depVersion) {
    //        for (WorkspacePackageJson candidateWorkspace : workspacePackageJsons.values()) {
    //            logger.info("Comparing {}/{} to {}/{}",
    //                depName, depVersion,
    //                candidateWorkspace.getPackageJson().name, candidateWorkspace.getPackageJson().version);
    //            String candidateWorkspaceVersion = "workspace:" + candidateWorkspace.getDirRelativePath();
    //            if (depName.equals(candidateWorkspace.getPackageJson().name) && (depVersion.equals(candidateWorkspaceVersion))) {
    //                logger.info("\tThey match; this is a workspace");
    //                return candidateWorkspace;
    //            }
    //        }
    //        logger.info("{}/{} is not a workspace", depName, depVersion);
    //        return null;
    //    }

    // TODO this is depIsWorkspace have suspiciously similar names
    // both belong in Workspaces class; are they different?
    //    private boolean isWorkspaceOLD(YarnLockResult yarnLockResult, com.synopsys.integration.bdio.model.dependencyid.DependencyId dependencyId) {
    //        for (String workspaceName : yarnLockResult.getWorkspacePackageJsons().keySet()) {
    //            String dependencyIdString = ((StringDependencyId) dependencyId).getValue();
    //            if (dependencyIdString.startsWith(workspaceName + "@")) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
}
