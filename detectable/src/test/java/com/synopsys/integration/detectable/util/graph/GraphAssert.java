package com.synopsys.integration.detectable.util.graph;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    protected final Forge forge;
    protected final DependencyGraph graph;
    protected final ExternalIdFactory externalIdFactory;

    public GraphAssert(final Forge forge, final DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public ExternalId hasRootDependency(final ExternalId externalId) {
        assert graph.getRootDependencyExternalIds().contains(externalId);
        return externalId;
    }

    public ExternalId hasDependency(final ExternalId externalId) {
        assert graph.hasDependency(externalId);
        return externalId;
    }

    public ExternalId hasNoDependency(final ExternalId externalId) {
        assert !graph.hasDependency(externalId);
        return externalId;
    }

    public void hasParentChildRelationship(final ExternalId parent, final ExternalId child) {
        assert graph.getChildrenExternalIdsForParent(parent).contains(child);
    }

    public void hasRelationshipCount(final ExternalId parent, final int count) {
        assert graph.getChildrenExternalIdsForParent(parent).size() == count;
    }

    public void hasRootSize(final int size) {
        assert graph.getRootDependencies().size() == size;
    }

}
