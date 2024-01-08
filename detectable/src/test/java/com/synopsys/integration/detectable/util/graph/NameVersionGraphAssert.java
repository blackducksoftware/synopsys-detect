package com.synopsys.integration.detectable.util.graph;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import java.util.Set;

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
        Set<ExternalId> childrenIds = graph.getChildrenExternalIdsForParent(externalIdFactory.createNameVersionExternalId(forge, parentName, parentVersion));
        ExternalId childId = externalIdFactory.createNameVersionExternalId(forge, childName, childVersion);
        boolean flag = false;
        for (ExternalId childKnownId : childrenIds) {
            flag = childKnownId.getName().equals(childId.getName()) 
                    && childKnownId.getVersion().equals(childId.getVersion()) 
                    && childKnownId.getForge().equals(childId.getForge());
            if (flag) {
                break;
            }
        }
        Assertions.assertTrue(flag, "Expected dependency " + parentName + " " + parentVersion + " to have dependency of " + childName + " " + childVersion);
    }
}
