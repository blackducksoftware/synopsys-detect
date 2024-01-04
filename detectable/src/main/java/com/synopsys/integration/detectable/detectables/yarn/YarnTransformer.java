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
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo;
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
        LazyBuilderMissingExternalIdHandler lazyBuilderHandler = getLazyBuilderHandler(externalDependencies);
        ExternalIdDependencyGraphBuilder rootGraphBuilder = new ExternalIdDependencyGraphBuilder();
        addRootDependenciesForProjectOrWorkspace(yarnLockResult, yarnLockResult.getRootPackageJson(), rootGraphBuilder);
        DependencyGraph rootGraph = buildGraphForProjectOrWorkspace(lazyBuilderHandler, rootGraphBuilder, yarnLockResult);
        codeLocations.add(new CodeLocation(rootGraph));
        ExternalIdDependencyGraphBuilder graphBuilder = new ExternalIdDependencyGraphBuilder();
        DependencyGraph workspaceGraph = buildGraphForProjectOrWorkspace(lazyBuilderHandler, graphBuilder, yarnLockResult);
        for (YarnWorkspace workspace : yarnLockResult.getWorkspaceData().getWorkspaces()) {
            logger.info("Workspace: {}", workspace.getName());
            if ((workspaceFilter == null) || workspaceFilter.shouldInclude(workspace.getWorkspacePackageJson().getDirRelativePath())) {
                logger.debug("Adding root dependencies for workspace: {}", workspace.getWorkspacePackageJson().getDirRelativePath());
                addRootDependenciesForProjectOrWorkspace(yarnLockResult, workspace.getWorkspacePackageJson().getPackageJson(), graphBuilder);
                ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspace.getWorkspacePackageJson().getDirRelativePath(), "local");
                codeLocations.add(new CodeLocation(workspaceGraph, workspaceExternalId));
            }
        }
        return codeLocations;
    }
    
    private void addRootDependenciesForProjectOrWorkspace(
        YarnLockResult yarnLockResult,
        NullSafePackageJson projectOrWorkspacePackageJson,
        ExternalIdDependencyGraphBuilder graphBuilder
    ) throws MissingExternalIdException {
        addRootDependenciesToGraph(graphBuilder, projectOrWorkspacePackageJson.getDependencies(), yarnLockResult.getWorkspaceData());
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION)) {
            addRootDependenciesToGraph(graphBuilder, projectOrWorkspacePackageJson.getDevDependencies(), yarnLockResult.getWorkspaceData());
        }
    }

    private DependencyGraph buildGraphForProjectOrWorkspace(
            LazyBuilderMissingExternalIdHandler lazyBuilderHandler,
            ExternalIdDependencyGraphBuilder graphBuilder,
            YarnLockResult yarnLockResult
    ) throws MissingExternalIdException {
        BasicDependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                LazyId id = generateComponentDependencyId(entryId.getName(), entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), generateComponentExternalId(entryId.getName(), entry.getVersion()));
                ExternalIdDependencyGraphBuilder.LazyDependencyInfo parentInfo = graphBuilder.checkAndHandleMissingExternalId(lazyBuilderHandler, id);
                Dependency parent = new Dependency(parentInfo.getName(), parentInfo.getVersion(), parentInfo.getExternalId(), null);
                for (YarnLockDependency dependency : entry.getDependencies()) {
                    if (!isWorkspace(yarnLockResult.getWorkspaceData(), dependency)) {
                        LazyId stringDependencyId = generateComponentDependencyId(dependency.getName(), dependency.getVersion());
                        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION) || !dependency.isOptional()) {
                            graphBuilder.addChildWithParent(stringDependencyId, id);
                            LazyDependencyInfo childInfo = graphBuilder.checkAndHandleMissingExternalId(lazyBuilderHandler, stringDependencyId);
                            Dependency child = new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId(), null);
                            mutableDependencyGraph.addChildWithParent(child, parent);
                        } else {
                            logger.trace("Excluding optional dependency: {}", stringDependencyId);
                        }
                    }
                }
                if (graphBuilder.getRootLazyIds().contains(id) || graphBuilder.getRootLazyIds().contains(parentInfo.getAliasId())) {
                    mutableDependencyGraph.addDirectDependency(parent);
                }
            }
        }
        return mutableDependencyGraph;
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

    private void addRootDependenciesToGraph(ExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, YarnWorkspaces workspaceData) {
        for (Map.Entry<String, String> rootDependency : rootDependenciesToAdd.entrySet()) {
            Optional<YarnWorkspace> dependencyWorkspace = workspaceData.lookup(rootDependency.getKey(), rootDependency.getValue());
            if (dependencyWorkspace.isPresent()) {
                logger.trace("Omitting dependency {}/{} because it's a workspace", rootDependency.getKey(), rootDependency.getValue());
            } else {
                LazyId stringDependencyId = generateComponentDependencyId(rootDependency.getKey(), rootDependency.getValue());
                logger.debug("Adding root dependency to graph: stringDependencyId: {}", stringDependencyId);
                graphBuilder.addChildToRoot(stringDependencyId);
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
