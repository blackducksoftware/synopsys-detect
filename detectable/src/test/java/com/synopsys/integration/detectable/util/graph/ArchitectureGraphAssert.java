package com.synopsys.integration.detectable.util.graph;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class ArchitectureGraphAssert extends GraphAssert {

    public ArchitectureGraphAssert(Forge forge, DependencyGraph graph) {
        super(forge, graph);
    }

    public ExternalId hasDependency(String name, String version, String architecture) {
        return this.hasDependency(externalIdFactory.createArchitectureExternalId(forge, name, version, architecture));
    }

    public ExternalId noDependency(String name, String version, String architecture) {
        return this.hasNoDependency(externalIdFactory.createArchitectureExternalId(forge, name, version, architecture));
    }
}
