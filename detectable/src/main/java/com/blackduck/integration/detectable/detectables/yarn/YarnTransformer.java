package com.blackduck.integration.detectable.detectables.yarn;

import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.graph.builder.LazyBuilderMissingExternalIdHandler;
import com.blackduck.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.blackduck.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo;
import com.blackduck.integration.bdio.graph.builder.LazyId;
import com.blackduck.integration.bdio.graph.builder.MissingExternalIdException;
import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.model.externalid.ExternalId;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.blackduck.integration.detectable.detectables.yarn.parse.YarnLockResult;
import com.blackduck.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.blackduck.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.blackduck.integration.detectable.detectables.yarn.workspace.YarnWorkspace;
import com.blackduck.integration.detectable.detectables.yarn.workspace.YarnWorkspaces;
import com.blackduck.integration.util.ExcludedIncludedWildcardFilter;
import com.blackduck.integration.util.NameVersion;
import java.util.HashMap;

public class YarnTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String STRING_ID_NAME_VERSION_SEPARATOR = "@";
    private final ExternalIdFactory externalIdFactory;
    private final Map<LazyId, Optional<NameVersion>> unMatchedDependencies = new HashMap<>();
    private final EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter;

    public YarnTransformer(ExternalIdFactory externalIdFactory, EnumListFilter<YarnDependencyType> yarnDependencyTypeFilter) {
        this.externalIdFactory = externalIdFactory;
        this.yarnDependencyTypeFilter = yarnDependencyTypeFilter;
    }
    
    public List<CodeLocation> generateCodeLocations(YarnLockResult yarnLockResult, List<NameVersion> externalDependencies)
        throws MissingExternalIdException {
        List<CodeLocation> codeLocations = new LinkedList<>();
        logger.debug("Adding root dependencies for project: {}:{}, externalDeps: {}", yarnLockResult.getRootPackageJson().getNameString(), yarnLockResult.getRootPackageJson().getVersionString(), externalDependencies.size());
        LazyBuilderMissingExternalIdHandler lazyBuilderHandler = getLazyBuilderHandler(externalDependencies);
        ExternalIdDependencyGraphBuilder rootGraphBuilder = new ExternalIdDependencyGraphBuilder();
        addRootDependenciesForProject(yarnLockResult, yarnLockResult.getRootPackageJson(), rootGraphBuilder);
        DependencyGraph rootGraph = buildGraphForProject(lazyBuilderHandler, rootGraphBuilder, yarnLockResult);
        codeLocations.add(new CodeLocation(rootGraph));
        return codeLocations;
    }
    
    public List<CodeLocation> generateCodeLocations(YarnLockResult yarnLockResult, List<NameVersion> externalDependencies, @Nullable ExcludedIncludedWildcardFilter workspaceFilter)
        throws MissingExternalIdException {
        List<CodeLocation> codeLocations = new LinkedList<>();
        logger.debug("Adding root dependencies for project: {}:{}", yarnLockResult.getRootPackageJson().getNameString(), yarnLockResult.getRootPackageJson().getVersionString());
        DependencyGraph rootProjectGraph = buildGraphForProjectOrWorkspace(yarnLockResult, yarnLockResult.getRootPackageJson(), externalDependencies);
        codeLocations.add(new CodeLocation(rootProjectGraph));
        for (YarnWorkspace workspace : yarnLockResult.getWorkspaceData().getWorkspaces()) {
            if ((workspaceFilter == null) || workspaceFilter.shouldInclude(workspace.getWorkspacePackageJson().getDirRelativePath())) {
                logger.debug("Adding root dependencies for workspace: {}", workspace.getWorkspacePackageJson().getDirRelativePath());
                DependencyGraph workspaceGraph = buildGraphForProjectOrWorkspace(yarnLockResult, workspace.getWorkspacePackageJson().getPackageJson(), externalDependencies);
                ExternalId workspaceExternalId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, workspace.getWorkspacePackageJson().getDirRelativePath(), "local");
                codeLocations.add(new CodeLocation(workspaceGraph, workspaceExternalId));
            }
        }

        return codeLocations;
    }

    private DependencyGraph buildGraphForProjectOrWorkspace(
        YarnLockResult yarnLockResult,
        NullSafePackageJson projectOrWorkspacePackageJson,
        List<NameVersion> externalDependencies
    ) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();
        addRootDependenciesForProjectOrWorkspace(graphBuilder, projectOrWorkspacePackageJson, yarnLockResult.getWorkspaceData());
        Map<LazyId, NameVersion> yarnDependencies = new HashMap<>();
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                LazyId id = generateComponentDependencyId(entryId.getName(), entryId.getVersion());
                graphBuilder.setDependencyInfo(id, entryId.getName(), entry.getVersion(), generateComponentExternalId(entryId.getName(), entry.getVersion()));
                yarnDependencies.putAll(addYarnLockDependenciesToGraph(yarnLockResult, graphBuilder, entry, id));
            }
        }
        return graphBuilder.build(getLazyBuilderHandler(externalDependencies, yarnDependencies));
    }

    private Map<LazyId, NameVersion> addYarnLockDependenciesToGraph(YarnLockResult yarnLockResult, LazyExternalIdDependencyGraphBuilder graphBuilder, YarnLockEntry entry, LazyId id) {
        Map<LazyId, NameVersion> yarnDependencies = new HashMap<>();
        for (YarnLockDependency dependency : entry.getDependencies()) {
            if (!isWorkspace(yarnLockResult.getWorkspaceData(), dependency)) {
                LazyId stringDependencyId = generateComponentDependencyId(dependency.getName(), dependency.getVersion());

                if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION) || !dependency.isOptional()) {
                    yarnDependencies.put(stringDependencyId, new NameVersion(dependency.getName(), dependency.getVersion()));
                    graphBuilder.addChildWithParent(stringDependencyId, id);
                } else {
                    logger.trace("Excluding optional dependency: {}", stringDependencyId);
                }
            }
        }
        return yarnDependencies;
    }
    
    private DependencyGraph buildGraphForProject(
            LazyBuilderMissingExternalIdHandler lazyBuilderHandler,
            ExternalIdDependencyGraphBuilder graphBuilder,
            YarnLockResult yarnLockResult
    ) throws MissingExternalIdException {
        BasicDependencyGraph mutableDependencyGraph = new BasicDependencyGraph();
        int countComponents = 0;
        Map<String, Map<String, String>> resolvedEntryIdVersionMap = new HashMap<>(yarnLockResult.getYarnLock().getEntries().size());
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            countComponents++;
            Map<String, String> entryIdsToResolvedVersionMap = new HashMap<>(entry.getIds().size());
            String entryName = entry.getIds().get(0).getName();
            if (shouldInclude(entryName, entry.getVersion())) {
                // Yarn and patch libraries should not be included in the graph.
                for (YarnLockEntryId entryId : entry.getIds()) {
                    entryIdsToResolvedVersionMap.put(entryId.getVersion(), entry.getVersion());
                }
                resolvedEntryIdVersionMap.put(entryName, entryIdsToResolvedVersionMap);
            }
        }
        
        for (YarnLockEntry entry : yarnLockResult.getYarnLock().getEntries()) {
            String entryName = entry.getIds().get(0).getName();
            if (shouldInclude(entryName, entry.getVersion())) {
                LazyId id = generateComponentDependencyId(entryName, entry.getVersion());
                graphBuilder.setDependencyInfo(id, entryName, entry.getVersion(), generateComponentExternalId(entryName, entry.getVersion()));
                LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo parentInfo = graphBuilder.checkAndHandleMissingExternalId(lazyBuilderHandler, id);
                Dependency parent = new Dependency(parentInfo.getName(), parentInfo.getVersion(), parentInfo.getExternalId(), null);
                mutableDependencyGraph.addDirectDependency(parent);
                collectYarnDependencies(lazyBuilderHandler, graphBuilder, mutableDependencyGraph, yarnLockResult, entry, resolvedEntryIdVersionMap, parent);
            }
        }
        return mutableDependencyGraph;
    }
    
    private boolean shouldInclude(String entryName, String entryVersion) {
        return !entryName.contains("@patch:") && !entryName.startsWith("yarnpkg") && !entryVersion.equalsIgnoreCase("0.0.0-use.local"); 
    }
    
    private void collectYarnDependencies(
            LazyBuilderMissingExternalIdHandler lazyBuilderHandler,
            ExternalIdDependencyGraphBuilder graphBuilder,
            BasicDependencyGraph mutableDependencyGraph,
            YarnLockResult yarnLockResult,
            YarnLockEntry entry,
            Map<String, Map<String, String>> resolvedEntryIdVersionMap,
            Dependency parent
            ) throws MissingExternalIdException {
        for (YarnLockDependency dependency : entry.getDependencies()) {
            if (!isWorkspace(yarnLockResult.getWorkspaceData(), dependency)) {
                String dependencyVersion = getDependencyVersion(resolvedEntryIdVersionMap, dependency);
                LazyId stringDependencyId = generateComponentDependencyId(dependency.getName(), dependencyVersion);
                includeNonProductionOrOptionalIfNeeded(dependency, dependencyVersion, parent,lazyBuilderHandler, graphBuilder, mutableDependencyGraph, stringDependencyId);
            }
        }
    }

    private String getDependencyVersion(Map<String, Map<String, String>> resolvedEntryIdVersionMap, YarnLockDependency dependency) {
        Map<String, String> idVersionMap = resolvedEntryIdVersionMap.get(dependency.getName());
        String dependencyVersion;
        if (idVersionMap != null) {
            dependencyVersion = idVersionMap.get(dependency.getVersion());
            if (dependencyVersion == null) {
                if (idVersionMap.values().isEmpty()) {
                    logger.warn("Dependency {} with version definition {} not found in the Yarn map entries {}", dependency.getName(), dependency.getVersion(), idVersionMap.toString());
                    dependencyVersion = dependency.getVersion();
                } else {
                    // 1. Choose first version.
                    dependencyVersion = (String) idVersionMap.values().toArray()[0];
                }
                // 2. Try to auto-resolve to one of the versions.
            }
        } else {
            dependencyVersion = dependency.getVersion();
        }
        return dependencyVersion;
    }

    private void includeNonProductionOrOptionalIfNeeded(YarnLockDependency dependency, String dependencyVersion, Dependency parent, LazyBuilderMissingExternalIdHandler lazyBuilderHandler, ExternalIdDependencyGraphBuilder graphBuilder, BasicDependencyGraph mutableDependencyGraph, LazyId stringDependencyId) throws MissingExternalIdException {
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION) || !dependency.isOptional()) {
            graphBuilder.setDependencyInfo(stringDependencyId, dependency.getName(), dependencyVersion, generateComponentExternalId(dependency.getName(), dependencyVersion));
            LazyDependencyInfo childInfo = graphBuilder.checkAndHandleMissingExternalId(lazyBuilderHandler, stringDependencyId);
            Dependency child = new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId(), null);
            mutableDependencyGraph.addChildWithParent(child, parent);

        } else {
            logger.trace("Excluding optional dependency: {}", stringDependencyId);
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
                Optional<NameVersion> nameVersionOptional;
                if (!unMatchedDependencies.containsKey(dependencyId)) {
                    String dependencyIdClean = dependencyId.toString().replace("\\/","/");
                    nameVersionOptional = getNameVersion(dependencyIdClean);
                    logger.warn("No standard NPM package identification details are available for '{}' from the yarn.lock file", dependencyIdClean);
                    unMatchedDependencies.put(dependencyId, nameVersionOptional);
                } else {
                    nameVersionOptional = unMatchedDependencies.get(dependencyId);
                }
                return generateComponentExternalId(nameVersionOptional.get().getName(), nameVersionOptional.get().getVersion());
            }
        };
    }

    private LazyBuilderMissingExternalIdHandler getLazyBuilderHandler(List<NameVersion> externalDependencies, Map<LazyId, NameVersion> yarnLockDependencies) {
        logger.info("externalDependencies size:{}, yarnLockDependencies.size:{}", externalDependencies.size(), yarnLockDependencies.values().size());
        return (dependencyId, lazyDependencyInfo) -> {
            if (yarnLockDependencies.containsKey(dependencyId)) {
                NameVersion matchedDependency = yarnLockDependencies.get(dependencyId);
                return generateComponentExternalId(matchedDependency.getName(), matchedDependency.getVersion());
            }
            Optional<NameVersion> nameVersionOptional;
            if (!unMatchedDependencies.containsKey(dependencyId)) {
                String dependencyIdClean = dependencyId.toString().replace("\\/","/");
                nameVersionOptional = getNameVersion(dependencyIdClean);
                logger.warn("No standard NPM package identification details are available for '{}' from the yarn.lock file", dependencyIdClean);
                unMatchedDependencies.put(dependencyId, nameVersionOptional);
            } else {
                nameVersionOptional = unMatchedDependencies.get(dependencyId);
            }
            return generateComponentExternalId(nameVersionOptional.get().getName(), nameVersionOptional.get().getVersion());
        };
    }
    
    private Optional<NameVersion> getNameVersion(String dependencyIdString) {
        logger.info("dependencyIdString:{}", dependencyIdString);
        int start, mid, end;
        String name;
        if ((start = dependencyIdString.indexOf("STRING\",\"")) > -1
                &&  (mid = dependencyIdString.indexOf(":", start)) > -1
                && (end = dependencyIdString.indexOf("\"]}", mid)) > -1) {
            name = dependencyIdString.substring(start + "STRING\",\"".length() + 1, mid);
            String version = dependencyIdString.substring(mid + 1, end);
            return Optional.of(new NameVersion(name, version));
        } else if ((start = dependencyIdString.indexOf("NAME_VERSION\",\"")) > -1
                && (mid = dependencyIdString.indexOf(":", start)) > -1
                && (end = dependencyIdString.indexOf("\"]}", mid)) > -1) {
            name = dependencyIdString.substring(start + "NAME_VERSION\",\"".length() + 1, mid);
            String version = dependencyIdString.substring(mid + 1, end);
            return Optional.of(new NameVersion(name, version));
        }
        return Optional.empty();
    }
    
    private void addRootDependenciesForProject(YarnLockResult yarnLockResult, NullSafePackageJson projectPackageJson, LazyExternalIdDependencyGraphBuilder graphBuilder) throws MissingExternalIdException {
        addRootDependenciesToGraph(graphBuilder, projectPackageJson.getDependencies(), yarnLockResult.getWorkspaceData());
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION)) {
            addRootDependenciesToGraph(graphBuilder, projectPackageJson.getDevDependencies(), yarnLockResult.getWorkspaceData());
        }
    }

    private void addRootDependenciesForProjectOrWorkspace(LazyExternalIdDependencyGraphBuilder graphBuilder, NullSafePackageJson rootPackageJson, YarnWorkspaces workspaceData) {
        addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDependencies(), workspaceData);
        if (yarnDependencyTypeFilter.shouldInclude(YarnDependencyType.NON_PRODUCTION)) {
            addRootDependenciesToGraph(graphBuilder, rootPackageJson.getDevDependencies(), workspaceData);
        }
    }

    private void addRootDependenciesToGraph(LazyExternalIdDependencyGraphBuilder graphBuilder, Map<String, String> rootDependenciesToAdd, YarnWorkspaces workspaceData) {
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
        return LazyId.fromNameAndVersion(name, version);
    }

    private ExternalId generateComponentExternalId(String name, String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, name, version);
    }

    private ExternalId generateComponentExternalId(LazyId dependencyId) {
        return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, dependencyId.toString().replace("\\/", "/"));
    }
}
