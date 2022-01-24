package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;

public class CargoLockTransformer {

    private final DependencyFactory dependencyFactory = new DependencyFactory(new ExternalIdFactory());

    private final Map<String, Dependency> packageMap = new HashMap<>(); //TODO: Remove state.

    public Optional<DependencyGraph> toDependencyGraph(CargoLock cargoLock) {
        return cargoLock.getPackages().map(this::parseDependencies);
    }

    private DependencyGraph parseDependencies(List<CargoLockPackage> lockPackages) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        determineRootPackages(lockPackages).stream()
            .map(packageMap::get)
            .forEach(graph::addChildToRoot);

        for (CargoLockPackage lockPackage : lockPackages) { //TODO: Do this with a stream and another method.
            if (!lockPackage.getDependencies().isPresent()) {
                continue;
            }
            List<String> trimmedDependencies = extractDependencyNames(lockPackage.getDependencies().get());
            for (String dependency : trimmedDependencies) {
                Dependency child = packageMap.get(dependency);
                Dependency parent = packageMap.get(lockPackage.getName().orElse(""));
                if (child != null && parent != null) {
                    graph.addChildWithParent(child, parent);
                }
            }
        }
        return graph;
    }

    private Set<String> determineRootPackages(List<CargoLockPackage> lockPackages) { //TODO: root packages should be identified by name AND version (ex. comp:1.0.0 is a root dep but comp:1.1.0 is a transitive of otherComp:1.0.0)
        Set<String> rootPackages = new HashSet<>();
        Set<String> dependencyPackages = new HashSet<>();

        for (CargoLockPackage lockPackage : lockPackages) {
            String projectName = lockPackage.getName().orElse("");
            String projectVersion = lockPackage.getVersion().orElse("");

            //TODO: Shouldn't there be a check here for duplicates/conflicts? new DetectableException("We found a package with no name, please contact Support.")); Throw when the situation actually makes no sense.
            packageMap.put(projectName, dependencyFactory.createNameVersionDependency(Forge.CRATES, projectName, projectVersion));
            rootPackages.add(projectName);
            lockPackage.getDependencies()
                .map(this::extractDependencyNames)
                .ifPresent(dependencyPackages::addAll);
        }
        rootPackages.removeAll(dependencyPackages); //TODO: Use the TBD RootPruningGraphUtil.

        return rootPackages;
    }

    //TODO: This could be done in a Parser, could do the project name validation.
    private List<String> extractDependencyNames(List<String> rawDependencies) { //TODO: should extract name and version of dependency (in case two components dependent on different versions of same component)
        return rawDependencies.stream()
            .map(dependency -> dependency.split(" ")[0])
            .collect(Collectors.toList());
    }
}
