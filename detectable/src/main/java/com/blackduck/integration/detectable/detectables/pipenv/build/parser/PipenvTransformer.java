package com.blackduck.integration.detectable.detectables.pipenv.build.parser;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectables.pipenv.build.model.PipFreeze;
import com.blackduck.integration.detectable.detectables.pipenv.build.model.PipFreezeEntry;
import com.blackduck.integration.detectable.detectables.pipenv.build.model.PipenvGraph;
import com.blackduck.integration.detectable.detectables.pipenv.build.model.PipenvGraphDependency;
import com.blackduck.integration.detectable.detectables.pipenv.build.model.PipenvGraphEntry;

public class PipenvTransformer {
    private final ExternalIdFactory externalIdFactory;

    public PipenvTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation transform(String projectName, String projectVersionName, PipFreeze pipFreeze, PipenvGraph pipenvGraph, boolean includeOnlyProjectTree) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        for (PipenvGraphEntry entry : pipenvGraph.getEntries()) {
            Dependency entryDependency = nameVersionToDependency(entry.getName(), entry.getVersion(), pipFreeze);
            List<Dependency> children = addDependenciesToGraph(entry.getChildren(), dependencyGraph, pipFreeze);
            if (matchesProject(entryDependency, projectName, projectVersionName)) { // The project appears as an entry, we don't want the project to be a dependency of itself.
                dependencyGraph.addChildrenToRoot(children);
            } else if (!includeOnlyProjectTree) { // Only add non-project matches if we are not project tree only.
                dependencyGraph.addChildToRoot(entryDependency);
                dependencyGraph.addParentWithChildren(entryDependency, children);
            }
        }

        ExternalId projectExternalId = externalIdFactory.createNameVersionExternalId(Forge.PYPI, projectName, projectVersionName);
        return new CodeLocation(dependencyGraph, projectExternalId);
    }

    private List<Dependency> addDependenciesToGraph(List<PipenvGraphDependency> graphDependencies, DependencyGraph graph, PipFreeze pipFreeze) {
        List<Dependency> dependencies = new ArrayList<>();
        for (PipenvGraphDependency graphDependency : graphDependencies) {
            Dependency dependency = nameVersionToDependency(graphDependency.getName(), graphDependency.getInstalledVersion(), pipFreeze);
            List<Dependency> children = addDependenciesToGraph(graphDependency.getChildren(), graph, pipFreeze);
            graph.addParentWithChildren(dependency, children);
            dependencies.add(dependency);
        }
        return dependencies;
    }

    private boolean matchesProject(Dependency dependency, String projectName, String projectVersion) {
        return dependency.getName() != null && dependency.getVersion() != null && dependency.getName().equals(projectName) && dependency.getVersion().equals(projectVersion);
    }

    private String findFrozenName(String name, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
            .map(PipFreezeEntry::getName)
            .filter(itName -> itName.equalsIgnoreCase(name))
            .findFirst()
            .orElse(name);
    }

    private String findFrozenVersion(String name, String unfrozenVersion, PipFreeze pipFreeze) {
        return pipFreeze.getEntries().stream()
            .filter(it -> it.getName().equalsIgnoreCase(name))
            .map(PipFreezeEntry::getVersion)
            .findFirst()
            .orElse(unfrozenVersion);
    }

    private Dependency nameVersionToDependency(String givenName, String givenVersion, PipFreeze pipFreeze) {
        String version = findFrozenVersion(givenName, givenVersion, pipFreeze);
        String name = findFrozenName(givenName, pipFreeze);
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.PYPI, name, version));
    }
}
