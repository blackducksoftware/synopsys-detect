/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.HashSet;
import java.util.LinkedList;
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
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
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
    public static final String STRING_ID_NAME_VERSION_SEPARATOR = "@";
    private final ExternalIdFactory externalIdFactory;
    private final Set<StringDependencyId> unMatchedDependencies = new HashSet<>();

    public YarnTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> generateCodeLocations(YarnLockResult yarnLockResult, boolean productionOnly,
        List<NameVersion> externalDependencies, @Nullable ExcludedIncludedWildcardFilter workspaceFilter) throws MissingExternalIdException {
        List<CodeLocation> codeLocations = new LinkedList<>();
        DependencyGraph rootProjectGraph = buildGraphForProjectOrWorkspace(yarnLockResult, yarnLockResult.getRootPackageJson(), productionOnly,
            externalDependencies);
        codeLocations.add(new CodeLocation(rootProjectGraph));
        for (YarnWorkspace projectOrWorkspace : yarnLockResult.getWorkspaceData().getWorkspaces()) {
            if ((workspaceFilter == null) || workspaceFilter.willInclude(projectOrWorkspace.getName().orElse(null))) {
                DependencyGraph workspaceGraph = buildGraphForProjectOrWorkspace(yarnLockResult, projectOrWorkspace.getWorkspacePackageJson().getPackageJson(), productionOnly,
                    externalDependencies);
                ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, projectOrWorkspace.getName().orElse("unknown"),
                    projectOrWorkspace.getVersionString());
                codeLocations.add(new CodeLocation(workspaceGraph, workspaceExternalId));
            }
        }

        return codeLocations;
    }

    private DependencyGraph buildGraphForProjectOrWorkspace(YarnLockResult yarnLockResult, NullSafePackageJson projectOrWorkspacePackageJson, boolean productionOnly,
        List<NameVersion> externalDependencies) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();
        addRootNodesToGraph(graphBuilder, projectOrWorkspacePackageJson, yarnLockResult.getWorkspaceData(), productionOnly);
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                StringDependencyId id = generateComponentDependencyId(entryId.getName(), entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), generateComponentExternalId(entryId.getName(), entry.getVersion()));
                addYarnLockDependenciesToGraph(yarnLockResult, productionOnly, graphBuilder, entry, id);
            }
        }
        return graphBuilder.build(getLazyBuilderHandler(externalDependencies));
    }

    private void addYarnLockDependenciesToGraph(YarnLockResult yarnLockResult, boolean productionOnly,
        LazyExternalIdDependencyGraphBuilder graphBuilder, YarnLockEntry entry, StringDependencyId id) {
        for (YarnLockDependency dependency : entry.getDependencies()) {
            if (!isWorkspace(yarnLockResult.getWorkspaceData(), dependency)) {
                StringDependencyId stringDependencyId = generateComponentDependencyId(dependency.getName(), dependency.getVersion());
                if (!productionOnly || !dependency.isOptional()) {
                    graphBuilder.addChildWithParent(stringDependencyId, id);
                } else {
                    logger.trace("Excluding optional dependency: {}", stringDependencyId.getValue());
                }
            }
        }
    }

    private boolean isWorkspace(YarnWorkspaces yarnWorkspaces, YarnLockDependency dependency) {
        Optional<YarnWorkspace> dependencyWorkspace = yarnWorkspaces.lookup(dependency);
        return dependencyWorkspace.isPresent();
    }

    private LazyBuilderMissingExternalIdHandler getLazyBuilderHandler(List<NameVersion> externalDependencies) {
        return (dependencyId, lazyDependencyInfo) -> {
            Optional<NameVersion> externalDependency = externalDependencies.stream().filter(it -> it.getName().equals(lazyDependencyInfo.getName())).findFirst();
            Optional<ExternalId> externalId = externalDependency.map(it -> generateComponentExternalId(it.getName(), it.getVersion()));
            if (externalId.isPresent()) {
                return externalId.get();
            } else {
                ExternalId lazilyGeneratedExternalId;
                StringDependencyId stringDependencyId = (StringDependencyId) dependencyId;
                if (!unMatchedDependencies.contains(stringDependencyId)) {
                    logger.warn("Unable to find standard NPM package identification details for '{}' in the yarn.lock file", stringDependencyId.getValue());
                    unMatchedDependencies.add(stringDependencyId);
                }
                lazilyGeneratedExternalId = generateComponentExternalId(stringDependencyId);
                return lazilyGeneratedExternalId;
            }
        };
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        NullSafePackageJson projectOrWorkspacePackageJson, YarnWorkspaces workspaceData, boolean productionOnly) {
        logger.debug("Adding root dependencies from project/workspace PackageJson: {}:{}", projectOrWorkspacePackageJson.getNameString(), projectOrWorkspacePackageJson.getVersionString());
        populateGraphWithRootDependencies(graphBuilder, projectOrWorkspacePackageJson, productionOnly, workspaceData);
    }

    private void populateGraphWithRootDependencies(LazyExternalIdDependencyGraphBuilder graphBuilder, NullSafePackageJson rootPackageJson, boolean productionOnly, YarnWorkspaces workspaceData) {
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDependencies(), workspaceData);
        if (!productionOnly) {
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDevDependencies(), workspaceData);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, YarnWorkspaces workspaceData) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd.entrySet()) {
            Optional<YarnWorkspace> dependencyWorkspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
            if (dependencyWorkspace.isPresent()) {
                logger.trace("Omitting dependency {}/{} because it's a workspace", rootDependency.getKey(), rootDependency.getValue());
            } else {
                StringDependencyId stringDependencyId;
                stringDependencyId = deriveIdForDependency(graphBuilder, workspaceData, rootDependency);
                logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
                graphBuilder.addChildToRoot(stringDependencyId);
            }
        }
    }

    private StringDependencyId deriveIdForDependency(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspaces workspaceData, Map.Entry<String, String> rootDependency) {
        StringDependencyId stringDependencyId;
        Optional<YarnWorkspace> workspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
        if (workspace.isPresent()) {
            stringDependencyId = workspace.get().generateDependencyId();
            ExternalId externalId = workspace.get().generateExternalId();
            graphBuilder
                .setDependencyInfo(stringDependencyId, workspace.get().getName().orElse(null), workspace.get().getVersion().orElse(null), externalId);
        } else {
            stringDependencyId = generateComponentDependencyId(rootDependency.getKey(), rootDependency.getValue());
        }
        return stringDependencyId;
    }

    private StringDependencyId generateComponentDependencyId(String name, String version) {
        return new StringDependencyId(name + STRING_ID_NAME_VERSION_SEPARATOR + version);
    }

    private ExternalId generateComponentExternalId(String name, String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
    }

    private ExternalId generateComponentExternalId(StringDependencyId dependencyId) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, dependencyId.getValue());
    }
}
