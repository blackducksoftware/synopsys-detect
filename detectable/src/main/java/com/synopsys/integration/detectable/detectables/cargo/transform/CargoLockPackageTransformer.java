package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.util.NameOptionalVersion;

public class CargoLockPackageTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transformToGraph(List<CargoLockPackage> lockPackages) throws MissingExternalIdException, DetectableException {
        verifyNoDuplicatePackages(lockPackages);

        LazyExternalIdDependencyGraphBuilder graph = new LazyExternalIdDependencyGraphBuilder();
        lockPackages.forEach(lockPackage -> {
            String parentName = lockPackage.getPackageNameVersion().getName();
            String parentVersion = lockPackage.getPackageNameVersion().getVersion();
            LazyId parentId = LazyId.fromNameAndVersion(parentName, parentVersion);
            Dependency parentDependency = dependencyFactory.createNameVersionDependency(Forge.CRATES, parentName, parentVersion);

            graph.addChildToRoot(parentId);
            graph.setDependencyInfo(parentId, parentDependency.getName(), parentDependency.getVersion(), parentDependency.getExternalId());
            graph.setDependencyAsAlias(parentId, LazyId.fromName(parentName));

            lockPackage.getDependencies().forEach(childPackage -> {
                if (childPackage.getVersion().isPresent()) {
                    LazyId childId = LazyId.fromNameAndVersion(childPackage.getName(), childPackage.getVersion().get());
                    graph.addChildWithParent(childId, parentId);
                } else {
                    LazyId childId = LazyId.fromName(childPackage.getName());
                    graph.addChildWithParent(childId, parentId);
                }
            });
        });

        return graph.build();
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
