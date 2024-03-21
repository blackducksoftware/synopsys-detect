package com.synopsys.integration.detectable.detectables.yarn;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyBuilderMissingExternalIdHandler;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExternalIdDependencyGraphBuilder extends LazyExternalIdDependencyGraphBuilder {
    
    public LazyDependencyInfo checkAndHandleMissingExternalId(LazyBuilderMissingExternalIdHandler lazyBuilderHandler, LazyId lazyId) throws MissingExternalIdException {
        LazyDependencyInfo lazyDependencyInfo = this.infoForIdCopy(lazyId);
        if (lazyDependencyInfo.getExternalId() == null) {
            ExternalId handledExternalId = lazyBuilderHandler.handleMissingExternalId(lazyId, lazyDependencyInfo);
            if (handledExternalId == null || lazyId == null) {
                throw new MissingExternalIdException(lazyId);
            } else {
                lazyDependencyInfo.setExternalId(handledExternalId);
            }
        }
        return lazyDependencyInfo;
    }
    
    public Set<LazyId> getRootLazyIds() {
        return rootLazyIds;
    }

    private final Set<LazyId> rootLazyIds = new HashSet<>();

    private final Map<LazyId, LazyDependencyInfo> dependencyInfo = new HashMap<>();

    /**
     * This method exactly duplicates the same method (minus -Copy suffix in method signature) from the parent class.
     * @param id
     * @return
     */
    private LazyDependencyInfo infoForIdCopy(LazyId id) {
        LazyDependencyInfo info = dependencyInfo.get(id);
        if (info.getAliasId() != null) {
            info = dependencyInfo.get(info.getAliasId());
        }
        return info;
    }

    @Override
    public BasicDependencyGraph build() throws MissingExternalIdException {
        return build((lazyId, lazyDependencyInfo) -> {
            if (lazyDependencyInfo != null && lazyDependencyInfo.getAliasId() != null) {
                throw new MissingExternalIdException(lazyDependencyInfo.getAliasId());
            } else {
                throw new MissingExternalIdException(lazyId);
            }
        });
    }

    @Override
    public BasicDependencyGraph build(LazyBuilderMissingExternalIdHandler lazyBuilderHandler) throws MissingExternalIdException {
        BasicDependencyGraph mutableDependencyGraph = new BasicDependencyGraph();

        for (LazyId lazyId : dependencyInfo.keySet()) {
            LazyDependencyInfo lazyDependencyInfo = infoForIdCopy(lazyId);
            if (lazyDependencyInfo.getExternalId() == null) {
                ExternalId handledExternalId = lazyBuilderHandler.handleMissingExternalId(lazyId, lazyDependencyInfo);
                if (handledExternalId == null || lazyId == null) {
                    throw new MissingExternalIdException(lazyId);
                } else {
                    lazyDependencyInfo.setExternalId(handledExternalId);
                }
            }
        }

        for (LazyId lazyId : dependencyInfo.keySet()) {
            LazyDependencyInfo lazyDependencyInfo = infoForIdCopy(lazyId);
            Dependency dependency = new Dependency(lazyDependencyInfo.getName(), lazyDependencyInfo.getVersion(), lazyDependencyInfo.getExternalId(), null);

            for (LazyId child : lazyDependencyInfo.getChildren()) {
                LazyDependencyInfo childInfo = infoForIdCopy(child);
                mutableDependencyGraph.addParentWithChild(dependency,
                        new Dependency(childInfo.getName(), childInfo.getVersion(), childInfo.getExternalId(), null));
            }

            if (rootLazyIds.contains(lazyId) || rootLazyIds.contains(lazyDependencyInfo.getAliasId())) {
                mutableDependencyGraph.addDirectDependency(dependency);
            }
        }

        return mutableDependencyGraph;
    }

    /**
     * This method exactly duplicates the same method (minus -Copy suffix in method signature) from the parent class.
     * @param lazyId
     */
    private void ensureDependencyInfoExistsCopy(LazyId lazyId) {
        dependencyInfo.computeIfAbsent(lazyId, key -> new LazyDependencyInfo());
    }

    @Override
    public void setDependencyAsAlias(LazyId realLazyId, LazyId fakeLazyId) {
        ensureDependencyInfoExistsCopy(realLazyId);
        ensureDependencyInfoExistsCopy(fakeLazyId);
        LazyDependencyInfo info = dependencyInfo.get(fakeLazyId);
        info.setAliasId(realLazyId);
    }

    @Override
    public void setDependencyInfo(LazyId id, String name, String version, ExternalId externalId) {
        ensureDependencyInfoExistsCopy(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
        info.setVersion(version);
        info.setExternalId(externalId);
    }

    @Override
    public void setDependencyName(LazyId id, String name) {
        ensureDependencyInfoExistsCopy(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setName(name);
    }

    @Override
    public void setDependencyVersion(LazyId id, String version) {
        ensureDependencyInfoExistsCopy(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setVersion(version);
    }

    @Override
    public void setDependencyExternalId(LazyId id, ExternalId externalId) {
        ensureDependencyInfoExistsCopy(id);
        LazyDependencyInfo info = dependencyInfo.get(id);
        info.setExternalId(externalId);
    }

    @Override
    public void addParentWithChild(LazyId parent, LazyId child) {
        ensureDependencyInfoExistsCopy(child);
        ensureDependencyInfoExistsCopy(parent);
        dependencyInfo.get(parent).getChildren().add(child);

    }

    @Override
    public void addParentWithChildren(LazyId parent, List<LazyId> children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    @Override
    public void addParentWithChildren(LazyId parent, Set<LazyId> children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    @Override
    public void addParentWithChildren(LazyId parent, LazyId... children) {
        for (LazyId child : children) {
            addParentWithChild(parent, child);
        }
    }

    @Override
    public void addChildWithParent(LazyId child, LazyId parent) {
        addParentWithChild(parent, child);
    }

    @Override
    public void addChildWithParents(LazyId child, List<LazyId> parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    @Override
    public void addChildWithParents(LazyId child, Set<LazyId> parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    @Override
    public void addChildWithParents(LazyId child, LazyId... parents) {
        for (LazyId parent : parents) {
            addChildWithParent(child, parent);
        }
    }

    @Override
    public void addChildToRoot(LazyId child) {
        ensureDependencyInfoExistsCopy(child);
        rootLazyIds.add(child);
    }

    @Override
    public void addChildrenToRoot(List<LazyId> children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(Set<LazyId> children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

    @Override
    public void addChildrenToRoot(LazyId... children) {
        for (LazyId child : children) {
            addChildToRoot(child);
        }
    }

}
