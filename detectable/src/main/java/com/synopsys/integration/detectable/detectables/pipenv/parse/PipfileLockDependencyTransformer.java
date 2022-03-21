package com.synopsys.integration.detectable.detectables.pipenv.parse;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;

public class PipfileLockDependencyTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transform(List<PipfileLockDependency> dependencies) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencies.stream()
            .map(this::createDependency)
            .forEach(dependencyGraph::addChildToRoot);
        return dependencyGraph;
    }

    private Dependency createDependency(PipfileLockDependency dependency) {
        return dependencyFactory.createNameVersionDependency(Forge.PYPI, dependency.getName(), dependency.getVersion());
    }
}
