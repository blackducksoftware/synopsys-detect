package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    private Forge forge;
    private final DependencyGraph graph;
    private ExternalIdFactory externalIdFactory;

    public  GraphAssert(final Forge forge, DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public ExternalId hasDependency(String name, String version, String architecture) {
        ExternalId id = externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
        assert graph.hasDependency(id);
        return id;
    }

    public ExternalId noDependency(String name, String version, String architecture) {
        ExternalId id = externalIdFactory.createArchitectureExternalId(forge, name, version, architecture);
        assert !graph.hasDependency(id);
        return id;
    }

    public void hasParentChildRelationship(ExternalId parent, ExternalId child){
        assert graph.getChildrenExternalIdsForParent(parent).contains(child);
    }

    public void relationshipCount(ExternalId parent, int count){
        assert graph.getChildrenExternalIdsForParent(parent).size() == count;
    }

    public void rootSize(int size){
        assert graph.getRootDependencies().size() == size;
    }

    public static void dependency(Forge forge, DependencyGraph graph, String name, String version, String architecture) {
        new GraphAssert(forge, graph).hasDependency(name, version, architecture);
    }
}
