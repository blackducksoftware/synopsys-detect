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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
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
import com.synopsys.integration.util.NameVersion;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public YarnTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph transform(YarnLockResult yarnLockResult, boolean productionOnly, boolean addWorkspaceDependencies, boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        List<NameVersion> externalDependencies) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        addRootNodesToGraph(graphBuilder, yarnLockResult.getRootPackageJson(), yarnLockResult.getWorkspacePackageJsons(), productionOnly,
            addWorkspaceDependencies, getWorkspaceDependenciesFromWorkspacePackageJson);

        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                StringDependencyId id = new StringDependencyId(entryId.getName() + "@" + entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), externalIdFactory.createNameVersionExternalId(Forge.NPMJS, entryId.getName(), entry.getVersion()));
                for (YarnLockDependency dependency : entry.getDependencies()) {
                    StringDependencyId stringDependencyId = new StringDependencyId(dependency.getName() + "@" + dependency.getVersion());
                    if (!productionOnly || !dependency.isOptional()) {
                        graphBuilder.addChildWithParent(stringDependencyId, id);
                    } else {
                        logger.debug("Excluding optional dependency: {}", stringDependencyId.getValue());
                    }
                }
            }
        }
        return graphBuilder.build((dependencyId, lazyDependencyInfo) -> {
            Optional<NameVersion> externalDependency = externalDependencies.stream().filter(it -> it.getName().equals(lazyDependencyInfo.getName())).findFirst();
            Optional<ExternalId> externalId = externalDependency.map(it -> externalIdFactory.createNameVersionExternalId(Forge.NPMJS, it.getName(), it.getVersion()));

            if (externalId.isPresent()) {
                return externalId.get();
            } else {
                StringDependencyId stringDependencyId = (StringDependencyId) dependencyId;
                if (isWorkspace(yarnLockResult, dependencyId)) {
                    logger.debug("Including workspace {} in the graph", stringDependencyId.getValue());
                } else {
                    logger.warn(String.format("Missing yarn dependency. '%s' is neither a defined workspace nor a dependency defined in %s.", stringDependencyId.getValue(), yarnLockResult.getYarnLockFilePath()));
                }
                return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, stringDependencyId.getValue());
            }
        });
    }

    private boolean isWorkspace(YarnLockResult yarnLockResult, com.synopsys.integration.bdio.model.dependencyid.DependencyId dependencyId) {
        for (String workspaceName : yarnLockResult.getWorkspacePackageJsons().keySet()) {
            String dependencyIdString = ((StringDependencyId) dependencyId).getValue();
            if (dependencyIdString.startsWith(workspaceName + "@")) {
                return true;
            }
        }
        return false;
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        PackageJson rootPackageJson, Map<String, PackageJson> workspacePackageJsons, boolean productionOnly,
        boolean addWorkspacesToRoot, boolean getWorkspaceDependenciesFromWorkspacePackageJson) {
        logger.debug("Adding root dependencies from root PackageJson: {}:{}", rootPackageJson.name, rootPackageJson.version);
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.dependencies.entrySet());
        if (!productionOnly) {
            logger.debug("\tAlso adding dev dependencies");
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.devDependencies.entrySet());
        }

        // TODO here: be selective about which workspaces to add
        // ExcludedIncludedWildcardFilter modulesFilter = ExcludedIncludedWildcardFilter.fromCollections(excludedModules, includedModules);
        if (addWorkspacesToRoot || getWorkspaceDependenciesFromWorkspacePackageJson) {
            for (PackageJson curWorkspacePackageJson : workspacePackageJsons.values()) {
                StringDependencyId workspaceId = new StringDependencyId(curWorkspacePackageJson.name + "@" + curWorkspacePackageJson.version);
                if (addWorkspacesToRoot) {
                    logger.debug("Adding root dependency representing workspace from workspace PackageJson: {}:{} ({})", curWorkspacePackageJson.name, curWorkspacePackageJson.version, workspaceId);
                    graphBuilder.addChildToRoot(workspaceId);
                }
                if (getWorkspaceDependenciesFromWorkspacePackageJson) {
                    addWorkspaceChildrenToGraph(graphBuilder, workspaceId, curWorkspacePackageJson.dependencies.entrySet());
                    if (!productionOnly) {
                        addWorkspaceChildrenToGraph(graphBuilder, workspaceId, curWorkspacePackageJson.devDependencies.entrySet());
                    }
                }
            }
        }
    }

    private void addWorkspaceChildrenToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, StringDependencyId workspaceId, Set<Map.Entry<String, String>> workspaceDependenciesToAdd) {
        for (Map.Entry<String, String> depOfWorkspace : workspaceDependenciesToAdd) {
            StringDependencyId depOfWorkspaceId = new StringDependencyId(depOfWorkspace.getKey() + "@" + depOfWorkspace.getValue());
            logger.debug("Adding dependency of workspace ({}) as child of workspace {}", depOfWorkspaceId, workspaceId);
            graphBuilder.addChildWithParent(depOfWorkspaceId, workspaceId);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Set<Map.Entry<String, String>> rootDependenciesToAdd) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd) {
            StringDependencyId stringDependencyId = new StringDependencyId(rootDependency.getKey() + "@" + rootDependency.getValue());
            logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
            graphBuilder.addChildToRoot(stringDependencyId);
        }
    }
}
