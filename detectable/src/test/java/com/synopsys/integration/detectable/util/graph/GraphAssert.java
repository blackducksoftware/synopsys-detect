package com.synopsys.integration.detectable.util.graph;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    protected Forge forge;
    protected final DependencyGraph graph;
    protected ExternalIdFactory externalIdFactory;

    public GraphAssert(final Forge forge, DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public static void assertGraph(final String s, final DependencyGraph projectDependencies) {
        // TODO: Implement me
    }

    public ExternalId hasRootDependency(ExternalId externalId) {
        assert graph.getRootDependencyExternalIds().contains(externalId);
        return externalId;
    }

    public ExternalId hasDependency(ExternalId externalId) {
        assert graph.hasDependency(externalId);
        return externalId;
    }

    public ExternalId noDependency(ExternalId externalId) {
        assert !graph.hasDependency(externalId);
        return externalId;
    }

    public void hasParentChildRelationship(ExternalId parent, ExternalId child) {
        assert graph.getChildrenExternalIdsForParent(parent).contains(child);
    }

    public void relationshipCount(ExternalId parent, int count) {
        assert graph.getChildrenExternalIdsForParent(parent).size() == count;
    }

    public void rootSize(int size) {
        assert graph.getRootDependencies().size() == size;
    }

}
