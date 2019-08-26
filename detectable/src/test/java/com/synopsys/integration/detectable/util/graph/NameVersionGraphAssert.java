package com.synopsys.integration.detectable.util.graph;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class NameVersionGraphAssert extends GraphAssert {
    public NameVersionGraphAssert(final Forge forge, final DependencyGraph graph) {
        super(forge, graph);
    }

    public ExternalId hasDependency(final String name, final String version) {
        return this.hasDependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }

    public ExternalId noDependency(final String name, final String version) {
        return this.hasNoDependency(externalIdFactory.createNameVersionExternalId(forge, name, version));
    }
}
