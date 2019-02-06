package com.synopsys.integration.detectable.util.graph;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class MavenGraphAssert extends GraphAssert {

    public MavenGraphAssert(final DependencyGraph graph) {
        super(Forge.MAVEN, graph);
    }

    private ExternalId gavToExternalId(String gav) {
        String[] pieces = gav.split(":");
        ExternalId id = externalIdFactory.createMavenExternalId(pieces[0], pieces[1], pieces[2]);
        return id;
    }

    public ExternalId hasRootDependency(String gav) {
        return this.hasRootDependency(gavToExternalId(gav));
    }

    public ExternalId hasDependency(String gav) {
        return this.hasDependency(gavToExternalId(gav));
    }

    public ExternalId noDependency(String gav) {
        return this.noDependency(gavToExternalId(gav));
    }
}
