package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.detectable.util.RootPruningGraphUtil;

public class CargoLockTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transformToGraph(List<CargoLockPackage> lockPackages) throws MissingExternalIdException, CycleDetectedException, DetectableException {
        verifyNoDuplicatePackages(lockPackages);

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

        RootPruningGraphUtil rootPruningGraphUtil = new RootPruningGraphUtil();
        return rootPruningGraphUtil.prune(graph.build());
    }

    private void verifyNoDuplicatePackages(List<CargoLockPackage> lockPackages) throws DetectableException {
        for (CargoLockPackage cargoLockPackage : lockPackages) {
            for (NameOptionalVersion dependency : cargoLockPackage.getDependencies()) {
                if (!dependency.getVersion().isPresent()) {
                    long matchingPackages = lockPackages.stream()
                        .filter(filteringPackage -> dependency.getName().equals(filteringPackage.getPackageNameVersion().getName()))
                        .count();
                    if (matchingPackages > 1) {
                        throw new DetectableException("Multiple packages with the same name cannot be reconciled to a single version.");
                    }
                }
            }
        }
    }

}
