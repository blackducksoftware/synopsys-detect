package com.synopsys.integration.detectable.util.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphAssert {
    protected final Forge forge;
    protected final DependencyGraph graph;
    protected final ExternalIdFactory externalIdFactory;

    public GraphAssert(Forge forge, DependencyGraph graph) {
        this.forge = forge;
        this.graph = graph;
        this.externalIdFactory = new ExternalIdFactory();
    }

    public ExternalId hasRootDependency(ExternalId externalId) {
        return hasRootDependency(externalId, String.format("Expected '%s' to be in the root of graph.", externalId.createExternalId()));
    }

    public ExternalId hasRootDependency(ExternalId externalId, String message) {
        boolean hasRootDependency = graph.getRootDependencies().stream()
            .map(Dependency::getExternalId)
            .anyMatch(externalId::equals);
        assertTrue(hasRootDependency, message);
        return externalId;
    }

    public ExternalId hasDependency(ExternalId externalId) {
        return hasDependency(externalId, String.format("Expected '%s' to be in the graph.", externalId.createExternalId()));
    }

    public ExternalId hasDependency(ExternalId externalId, String message) {
        assertTrue(graph.hasDependency(externalId), message);
        return externalId;
    }

    public ExternalId hasNoDependency(ExternalId externalId) {
        return hasNoDependency(externalId, String.format("Did not expect '%s' to be in the graph.", externalId.createExternalId()));
    }

    public ExternalId hasNoDependency(ExternalId externalId, String message) {
        assertFalse(graph.hasDependency(externalId), message);
        return externalId;
    }

    public ExternalId hasParentChildRelationship(ExternalId parent, ExternalId child) {
        return hasParentChildRelationship(parent, child, String.format("Expected parent '%s' to have child '%s'.", parent.createExternalId(), child.createExternalId()));
    }

    public ExternalId hasParentChildRelationship(ExternalId parent, ExternalId child, String message) {
        assertTrue(graph.getChildrenExternalIdsForParent(parent).contains(child), message);
        return child;
    }

    public void hasRelationshipCount(ExternalId parent, int count) {
        hasRelationshipCount(parent, count, String.format("Expected '%s' to have a relationship count of %d.", parent.createExternalId(), count));
    }

    public void hasRelationshipCount(ExternalId parent, int count, String message) {
        assertEquals(count, graph.getChildrenExternalIdsForParent(parent).size(), message);
    }

    public void hasRootSize(int size) {
        hasRootSize(size, String.format("Graph should have a root size of %d.", size));
    }

    public void hasRootSize(int size, String message) {
        assertEquals(size, graph.getRootDependencies().size(), message);
    }

}
