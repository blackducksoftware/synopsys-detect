package com.synopsys.integration.detectable.detectables.bazel;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class DependencyTransformer {

    public CodeLocation createCodeLocation(List<Dependency> dependencies) {
        MutableDependencyGraph dependencyGraph = createDependencyGraph(dependencies);
        return new CodeLocation(dependencyGraph);
    }

    @NotNull
    private MutableDependencyGraph createDependencyGraph(List<Dependency> dependencies) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencies.forEach(dependencyGraph::addChildToRoot);
        return dependencyGraph;
    }
}
