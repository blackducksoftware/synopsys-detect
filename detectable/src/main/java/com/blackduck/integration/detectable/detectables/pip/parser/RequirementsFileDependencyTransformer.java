package com.blackduck.integration.detectable.detectables.pip.parser;

import java.util.List;

import com.blackduck.integration.bdio.graph.BasicDependencyGraph;
import com.blackduck.integration.bdio.graph.DependencyGraph;
import com.blackduck.integration.bdio.model.Forge;
import com.blackduck.integration.bdio.model.dependency.Dependency;
import com.blackduck.integration.bdio.model.dependency.DependencyFactory;
import com.blackduck.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.python.util.PythonDependency;

public class RequirementsFileDependencyTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transform(List<PythonDependency> dependencies) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        dependencies.stream()
            .map(this::createDependency)
            .forEach(dependencyGraph::addChildToRoot);
        return dependencyGraph;
    }

    private Dependency createDependency(PythonDependency dependency) {
        return dependencyFactory.createNameVersionDependency(Forge.PYPI, dependency.getName(), dependency.getVersion());
    }
}
