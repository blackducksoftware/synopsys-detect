/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

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

    public YarnTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public List<CodeLocation> generateCodeLocations(YarnLockResult yarnLockResult, boolean productionOnly, boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        List<NameVersion> externalDependencies, @Nullable ExcludedIncludedWildcardFilter workspaceFilter) throws MissingExternalIdException {
        List<CodeLocation> codeLocations = new LinkedList<>();
        DependencyGraph rootProjectGraph = buildGraphForProjectOrWorkspace(yarnLockResult, yarnLockResult.getRootPackageJson(), productionOnly, getWorkspaceDependenciesFromWorkspacePackageJson,
            externalDependencies);
        CodeLocation rootProjectCodeLocation = new CodeLocation(rootProjectGraph);
        codeLocations.add(rootProjectCodeLocation);
        for (YarnWorkspace projectOrWorkspace : yarnLockResult.getWorkspaceData().getWorkspaces()) {
            if (isRootProject(yarnLockResult, projectOrWorkspace)) {
                System.out.println("TODO: I WONDERED: IS THIS EVER TRUE?? Yup, it is!");
                continue;
            }
            if ((workspaceFilter == null) || workspaceFilter.willInclude(projectOrWorkspace.getName().orElse(null))) {
                DependencyGraph workspaceGraph = buildGraphForProjectOrWorkspace(yarnLockResult, projectOrWorkspace.getWorkspacePackageJson().getPackageJson(), productionOnly, getWorkspaceDependenciesFromWorkspacePackageJson,
                    externalDependencies);
                ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(new Forge("/", "Detect"),
                    projectOrWorkspace.getName().orElse("unknown"),
                    projectOrWorkspace.getVersionString());
                codeLocations.add(new CodeLocation(workspaceGraph, workspaceExternalId));
            }
        }

        return codeLocations;
    }

    private boolean isRootProject(YarnLockResult yarnLockResult, YarnWorkspace projectOrWorkspace) {
        return yarnLockResult.getRootPackageJson().getName().equals(projectOrWorkspace.getName().orElse("")) &&
                   yarnLockResult.getRootPackageJson().getVersion().equals(projectOrWorkspace.getVersionString());
    }

    private DependencyGraph buildGraphForProjectOrWorkspace(YarnLockResult yarnLockResult, NullSafePackageJson projectOrWorkspacePackageJson, boolean productionOnly, boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        List<NameVersion> externalDependencies) throws MissingExternalIdException {
        // TODO all of this needs to be done per workspace?
        // So far: Only adds rootProject's dependencies and transitives
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        // TODO/done omit workspaces
        addRootNodesToGraph(graphBuilder, projectOrWorkspacePackageJson, yarnLockResult.getWorkspaceData(), productionOnly,
            getWorkspaceDependenciesFromWorkspacePackageJson);

        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            // TODO:
            //            Optional<YarnWorkspace> workspace = yarnLockResult.getWorkspaceData().lookup(entry);
            //            if (workspace.isPresent()) {
            //                StringDependencyId id = workspace.get().createDependency(graphBuilder);
            //                addYarnLockDependenciesToGraph(yarnLockResult, productionOnly, graphBuilder, entry, id, getWorkspaceDependencyQualifiesCheck(workspace.get(), productionOnly));
            //            } else {
            for (YarnLockEntryId entryId : entry.getIds()) {
                StringDependencyId id = generateComponentDependencyId(entryId.getName(), entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), generateComponentExternalId(entryId.getName(), entry.getVersion()));
                addYarnLockDependenciesToGraph(yarnLockResult, productionOnly, graphBuilder, entry, id, getEverythingQualifiesCheck());
            }
            //            }
        }
        return graphBuilder.build(getLazyBuilderHandler(externalDependencies, yarnLockResult));
    }

    private Predicate<String> getEverythingQualifiesCheck() {
        return s -> true;
    }

    private Predicate<String> getWorkspaceDependencyQualifiesCheck(YarnWorkspace workspace, boolean productionOnly) {
        if (!productionOnly) {
            return getEverythingQualifiesCheck();
        }
        return depName -> thisWorkspaceDependencyIsProduction(workspace, depName);
    }

    private boolean thisWorkspaceDependencyIsProduction(YarnWorkspace workspace, String depName) {
        String workspaceName = workspace.getName().orElse("?");
        if (workspace.hasDependency(depName)) {
            logger.trace("Including workspace {} dependency {} because it's in the workspace package.json dependency list",
                workspaceName, depName);
            return true;
        }
        if (workspace.hasDevDependency(depName)) {
            logger.trace("Excluding workspace {} dependency {} because it's in the workspace package.json dev dependency list",
                workspaceName, depName);
            return false;
        }
        logger.warn("Workspace {} dependency {} was found in the workspace's yarn.lock entry, but not found in either of the workspace's package.json dependency lists; excluding it",
            workspaceName, depName);
        return false;
    }

    private void addYarnLockDependenciesToGraph(YarnLockResult yarnLockResult, boolean productionOnly,
        LazyExternalIdDependencyGraphBuilder graphBuilder, YarnLockEntry entry, StringDependencyId id,
        Predicate<String> qualificationCheck) {
        for (YarnLockDependency dependency : entry.getDependencies()) {
            if (qualificationCheck.test(dependency.getName())) {
                Optional<YarnWorkspace> dependencyWorkspace = yarnLockResult.getWorkspaceData().lookup(dependency);
                StringDependencyId stringDependencyId;
                if (dependencyWorkspace.isPresent()) { // TODO if wksp: SKIP?
                    stringDependencyId = dependencyWorkspace.get().generateDependencyId();
                } else {
                    stringDependencyId = generateComponentDependencyId(dependency.getName(), dependency.getVersion());
                }
                if (!productionOnly || !dependency.isOptional()) {
                    graphBuilder.addChildWithParent(stringDependencyId, id);
                } else {
                    logger.trace("Excluding optional dependency: {}", stringDependencyId.getValue());
                }
            }
        }
    }

    private LazyBuilderMissingExternalIdHandler getLazyBuilderHandler(List<NameVersion> externalDependencies, YarnLockResult yarnLockResult) {
        return (dependencyId, lazyDependencyInfo) -> {
            Optional<NameVersion> externalDependency = externalDependencies.stream().filter(it -> it.getName().equals(lazyDependencyInfo.getName())).findFirst();
            Optional<ExternalId> externalId = externalDependency.map(it -> generateComponentExternalId(it.getName(), it.getVersion()));
            if (externalId.isPresent()) {
                return externalId.get();
            } else {
                ExternalId lazilyGeneratedExternalId;
                StringDependencyId stringDependencyId = (StringDependencyId) dependencyId;
                Optional<YarnWorkspace> workspace = yarnLockResult.getWorkspaceData().lookup(stringDependencyId);
                if (workspace.isPresent()) {
                    logger.warn("Workspace {} wasn't defined during data collection for the graph build; adding it during build step", stringDependencyId.getValue());
                    lazilyGeneratedExternalId = workspace.get().generateExternalId();
                } else {
                    logger.warn("Missing yarn dependency. '{}' is neither a defined workspace nor a dependency defined in yarn.lock.", stringDependencyId.getValue());
                    lazilyGeneratedExternalId = generateComponentExternalId(stringDependencyId);
                }
                return lazilyGeneratedExternalId;
            }
        };
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder,
        NullSafePackageJson projectOrWorkspacePackageJson, YarnWorkspaces workspaceData, boolean productionOnly,
        boolean getWorkspaceDependenciesFromWorkspacePackageJson) {
        // TODO why is getWorkspaceDependenciesFromWorkspacePackageJson UNUSED???
        // TODO IF PROCESSING THE ROOT PROJECT, DO THIS populateGraphWithRootDependencies()
        // TODO used to filter workspaces here but I removed it
        // TODO pretty sure I no longer need NullSafePackageJson.isRoot!
        logger.debug("Adding root dependencies from project/workspace PackageJson: {}:{}", projectOrWorkspacePackageJson.getNameString(), projectOrWorkspacePackageJson.getVersionString());
        populateGraphWithRootDependencies(graphBuilder, projectOrWorkspacePackageJson, productionOnly, workspaceData);

        // TODO:
        //        if ((workspacesFilter != null) || getWorkspaceDependenciesFromWorkspacePackageJson) {
        //            populateGraphFromWorkspaceData(graphBuilder, workspaceData, productionOnly, getWorkspaceDependenciesFromWorkspacePackageJson, workspacesFilter);
        //        }
    }

    private void populateGraphWithRootDependencies(LazyExternalIdDependencyGraphBuilder graphBuilder, NullSafePackageJson rootPackageJson, boolean productionOnly, YarnWorkspaces workspaceData) {
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDependencies(), workspaceData);
        if (!productionOnly) {
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDevDependencies(), workspaceData);
        }
    }

    private void populateGraphFromWorkspaceData(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspaces workspaceData, boolean productionOnly,
        boolean getWorkspaceDependenciesFromWorkspacePackageJson,
        @Nullable ExcludedIncludedWildcardFilter workspacesFilter) {
        for (YarnWorkspace curWorkspace : workspaceData.getWorkspaces()) {
            StringDependencyId workspaceId = curWorkspace.generateDependencyId();
            if ((workspacesFilter != null) && workspacesFilter.willInclude(curWorkspace.getName().orElse(null))) {
                logger.debug("Adding root dependency representing workspace included by filter from workspace PackageJson: {}:{} ({})", curWorkspace.getName(),
                    curWorkspace.getVersion(),
                    workspaceId);
                graphBuilder.addChildToRoot(workspaceId);
                ExternalId externalId = curWorkspace.generateExternalId();
                graphBuilder.setDependencyInfo(workspaceId, curWorkspace.getName().orElse(null), curWorkspace.getVersion().orElse(null), externalId);
            }
            if (getWorkspaceDependenciesFromWorkspacePackageJson) {
                addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getDependencies().entrySet());
                if (!productionOnly) {
                    addWorkspaceChildrenToGraph(graphBuilder, workspaceData, workspaceId, curWorkspace.getDevDependencies().entrySet());
                }
            }
        }
    }

    private void addWorkspaceChildrenToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspaces workspaceData, StringDependencyId workspaceId, Set<Map.Entry<String, String>> workspaceDependenciesToAdd) {
        for (Map.Entry<String, String> depOfWorkspace : workspaceDependenciesToAdd) {
            StringDependencyId depOfWorkspaceId = deriveIdForDependency(graphBuilder, workspaceData, depOfWorkspace);
            logger.trace("Adding dependency of workspace ({}) as child of workspace {}", depOfWorkspaceId, workspaceId);
            graphBuilder.addChildWithParent(depOfWorkspaceId, workspaceId);
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
