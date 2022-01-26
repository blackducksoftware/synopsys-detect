package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.util.RootPruningGraphUtil;

public class CargoLockTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transformToGraph(List<CargoLockPackage> lockPackages) throws MissingExternalIdException, RootPruningGraphUtil.CycleDetectedException {
        LazyExternalIdDependencyGraphBuilder graph = new LazyExternalIdDependencyGraphBuilder();
        lockPackages.forEach(lockPackage -> {
            NameVersionDependencyId id = new NameVersionDependencyId(lockPackage.getPackageNameVersion().getName(), lockPackage.getPackageNameVersion().getVersion());
            Dependency nameVersionDependency = dependencyFactory.createNameVersionDependency(Forge.CRATES, id.getName(), id.getVersion());

            graph.addChildToRoot(nameVersionDependency);
            lockPackage.getDependencies().forEach(dependency -> {
                if (dependency.getVersion().isPresent()) {
                    NameVersionDependencyId dependencyId = new NameVersionDependencyId(dependency.getName(), dependency.getVersion().get());
                    Dependency childDependency = dependencyFactory.createNameVersionDependency(Forge.CRATES, dependencyId.getName(), dependencyId.getVersion());
                    graph.addChildWithParent(childDependency, dependencyId);
                    graph.setDependencyInfo(dependencyId, childDependency.getName(), childDependency.getVersion(), childDependency.getExternalId());
                } else {
                    NameDependencyId dependencyId = new NameDependencyId(dependency.getName());
                    Dependency childDependency = dependencyFactory.createNameVersionDependency(Forge.CRATES, dependencyId.getName());
                    graph.addChildWithParent(childDependency, dependencyId);
                    graph.setDependencyInfo(dependencyId, childDependency.getName(), childDependency.getVersion(), childDependency.getExternalId());
                }
            });
        });

        DependencyGraph builtGraph = graph.build((dependencyId, lazyDependencyInfo) -> {
            if (dependencyId instanceof Dependency) {
                Dependency id = (Dependency) dependencyId;
                return findDependencyByName(lockPackages, id.getName()).orElse(null);
            } else if (dependencyId instanceof NameDependencyId) {
                NameDependencyId id = (NameDependencyId) dependencyId;
                return findDependencyByName(lockPackages, id.getName()).orElse(null);
            }
            return null;
        });

        RootPruningGraphUtil rootPruningGraphUtil = new RootPruningGraphUtil();
        return rootPruningGraphUtil.prune(builtGraph);
    }

    private Optional<ExternalId> findDependencyByName(List<CargoLockPackage> lockPackages, String dependencyName) {
        return lockPackages.stream()
            .filter(cargoPackage -> cargoPackage.getPackageNameVersion().getName().equals(dependencyName))
            .map(cargoPackage -> externalIdFactory.createNameVersionExternalId(Forge.CRATES, cargoPackage.getPackageNameVersion().getName(), cargoPackage.getPackageNameVersion().getVersion()))
            .findFirst();
    }
}
