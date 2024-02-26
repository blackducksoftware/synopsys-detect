package com.synopsys.integration.detectable.util.graph;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class NameVersionGraphAssert extends GraphAssert {
    public NameVersionGraphAssert(Forge forge, DependencyGraph graph) {
        super(forge, graph);
    }

    public ExternalId hasRootDependency(String name, String version) {
        return this.hasRootDependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public ExternalId hasDependency(String name, String version) {
        return this.hasDependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public ExternalId hasNoDependency(String name, String version) {
        return this.hasNoDependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public void hasParentChildRelationship(String parentName, String parentVersion, String childName, String childVersion) {
        Assertions.assertTrue(
            graph.getChildrenExternalIdsForParent(externalIdFactory.createNameVersionExternalId(forge, parentName, parentVersion))
                .contains(externalIdFactory.createNameVersionExternalId(forge, childName, childVersion)),
            "Expected dependency " + parentName + " " + parentVersion + " to have dependency of " + childName + " " + childVersion
        );
    }
}
