package com.synopsys.integration.detectable.detectables.yarn;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
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
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspace;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;
import com.synopsys.integration.util.NameVersion;
import java.util.ArrayList;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String STRING_ID_NAME_VERSION_SEPARATOR = "@";
    private final ExternalIdFactory externalIdFactory;
    private final Set<LazyId> unMatchedDependencies = new HashSet<>();
    
    private final EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter;

    public YarnTransformer(ExternalIdFactory externalIdFactory, EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.yarnDependencyTypeFilter = yarnDependencyTypeFilter;
    }

    public List<CodeLocation> generateCodeLocations(YarnLockResult yarnLockResult, List<NameVersion> externalDependencies, @Nullable ExcludedIncludedWildcardFilter workspaceFilter)
        throws MissingExternalIdException {
        List<CodeLocation> codeLocations = new LinkedList<>();
        logger.debug("Adding root dependencies for project: {}:{}", yarnLockResult.getRootPackageJson().getNameString(), yarnLockResult.getRootPackageJson().getVersionString());
        LazyExternalIdDependencyGraphBuilder rootGraphBuilder = new LazyExternalIdDependencyGraphBuilder();
        Set<LazyId> rootLazyIds = new HashSet<>();
        addRootNodesToGraph(rootGraphBuilder, yarnLockResult.getRootPackageJson(), yarnLockResult.getWorkspaceData(), rootLazyIds);
        List<Dependency> rootDirectDependencies = buildGraphForProjectOrWorkspace(yarnLockResult, rootGraphBuilder, externalDependencies, rootLazyIds);
        BasicDependencyGraph rootGraph = new BasicDependencyGraph();
        rootDirectDependencies.stream().forEach(child -> rootGraph.addDirectDependency(child));
        codeLocations.add(new CodeLocation(rootGraph));
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();
        Set<LazyId> workspaceRootLazyIds = new HashSet<>();
        for (YarnWorkspace workspace : yarnLockResult.getWorkspaceData().getWorkspaces()) {
            if ((workspaceFilter == null) || workspaceFilter.shouldInclude(workspace.getWorkspacePackageJson().getDirRelativePath())) {
                BasicDependencyGraph workspaceGraph = new BasicDependencyGraph();
                logger.debug("Adding root dependencies for workspace: {}", workspace.getWorkspacePackageJson().getDirRelativePath());
                addDirectNodesToGraph(graphBuilder, workspace, yarnLockResult.getWorkspaceData(), workspaceRootLazyIds);
                ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspace.getWorkspacePackageJson().getDirRelativePath(), "local");
                codeLocations.add(new CodeLocation(workspaceGraph, workspaceExternalId));
            }
        }

        return codeLocations;
    }

    private List<Dependency> buildGraphForProjectOrWorkspace(
            YarnLockResult yarnLockResult,
            LazyExternalIdDependencyGraphBuilder graphBuilder,
            List<NameVersion> externalDependencies,
            Set<LazyId> rootLazyIds
    ) throws MissingExternalIdException {
        List<Dependency> directDependencies = new ArrayList<>();
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                LazyId id = generateComponentDependencyId(entryId.getName(), entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), generateComponentExternalId(entryId.getName(), entry.getVersion()));
                LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo parentInfo = graphBuilder.checkAndHandleMissingExternalId(getLazyBuilderHandler(externalDependencies), id);
                Dependency parent = new Dependency(parentInfo.getName(), parentInfo.getVersion(), parentInfo.getExternalId(), null);
                addYarnLockDependenciesToGraph(yarnLockResult, graphBuilder, entry, id);
                if (rootLazyIds.contains(id) || rootLazyIds.contains(parentInfo.getAliasId())) {
                    directDependencies.add(parent);
                }
            }
        }
        return directDependencies;
    }

    private void addYarnLockDependenciesToGraph(YarnLockResult yarnLockResult, LazyExternalIdDependencyGraphBuilder graphBuilder, YarnLockEntry entry, LazyId id) {
        for (YarnLockDependency dependency : entry.getDependencies()) {
            if (!isWorkspace(yarnLockResult.getWorkspaceData(), dependency)) {
                LazyId stringDependencyId = generateComponentDependencyId(dependency.getName(), dependency.getVersion());

                if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION) || !dependency.isOptional()) {
                    graphBuilder.addChildWithParent(stringDependencyId, id);
                } else {
                    logger.trace("Excluding optional dependency: {}", stringDependencyId);
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
                if (!unMatchedDependencies.contains(dependencyId)) {
                    logger.warn("Unable to find standard NPM package identification details for '{}' in the yarn.lock file", dependencyId);
                    unMatchedDependencies.add(dependencyId);
                }
                lazilyGeneratedExternalId = generateComponentExternalId(dependencyId);
                return lazilyGeneratedExternalId;
            }
        };
    }

    private void addRootNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, NullSafePackageJson projectOrWorkspacePackageJson, YarnWorkspaces workspaceData, Set<LazyId> rootLazyIds) {
        addRootDependenciesToGraph(graphBuilder, projectOrWorkspacePackageJson.getDependencies(), workspaceData, rootLazyIds);
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION)) {
            addRootDependenciesToGraph(graphBuilder, projectOrWorkspacePackageJson.getDevDependencies(), workspaceData, rootLazyIds);
        }
    }
    
    private void addDirectNodesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, YarnWorkspace workspace, YarnWorkspaces workspaceData, Set<LazyId> rootLazyIds) {
        addRootDependenciesToGraph(graphBuilder, workspace.getDependencies(), workspaceData, rootLazyIds);
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION)) {
            addRootDependenciesToGraph(graphBuilder, workspace.getDevDependencies(), workspaceData, rootLazyIds);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, YarnWorkspaces workspaceData, Set<LazyId> rootLazyIds) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd.entrySet()) {
            Optional<YarnWorkspace> dependencyWorkspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
            if (dependencyWorkspace.isPresent()) {
                logger.trace("Omitting dependency {}/{} because it's a workspace", rootDependency.getKey(), rootDependency.getValue());
            } else {
                LazyId stringDependencyId = generateComponentDependencyId(rootDependency.getKey(), rootDependency.getValue());
                logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
                graphBuilder.addChildToRoot(stringDependencyId);
                rootLazyIds.add(stringDependencyId);
            }
        }
    }

    private LazyId generateComponentDependencyId(String name, String version) {
        return LazyId.fromString(name + STRING_ID_NAME_VERSION_SEPARATOR + version);
    }

    private ExternalId generateComponentExternalId(String name, String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
    }

    private ExternalId generateComponentExternalId(LazyId dependencyId) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, dependencyId.toString());
    }
}
