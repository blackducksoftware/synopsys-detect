package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.List;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class RequirementsFileDependencyTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transform(List<RequirementsFileDependency> dependencies) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        dependencies.stream()
            .map(this::createDependency)
            .forEach(dependencyGraph::addChildToRoot);
        return dependencyGraph;
    }

    private Dependency createDependency(RequirementsFileDependency dependency) {
        return dependencyFactory.createNameVersionDependency(Forge.PYPI, dependency.getName(), dependency.getVersion());
    }
}
