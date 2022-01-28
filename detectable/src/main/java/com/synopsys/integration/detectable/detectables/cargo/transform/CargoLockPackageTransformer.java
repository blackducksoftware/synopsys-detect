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

public class CargoLockPackageTransformer {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    private final DependencyFactory dependencyFactory = new DependencyFactory(externalIdFactory);

    public DependencyGraph transformToGraph(List<CargoLockPackage> lockPackages) throws MissingExternalIdException, CycleDetectedException, DetectableException {
        verifyNoDuplicatePackages(lockPackages);

        LazyExternalIdDependencyGraphBuilder graph = new LazyExternalIdDependencyGraphBuilder();
        lockPackages.forEach(lockPackage -> {
            NameVersionDependencyId parentId = new NameVersionDependencyId(lockPackage.getPackageNameVersion().getName(), lockPackage.getPackageNameVersion().getVersion());
            Dependency parentDependency = dependencyFactory.createNameVersionDependency(Forge.CRATES, parentId.getName(), parentId.getVersion());

            graph.addChildToRoot(parentId);
            graph.setDependencyInfo(parentId, parentDependency.getName(), parentDependency.getVersion(), parentDependency.getExternalId());
            graph.setDependencyAsAlias(parentId, new NameDependencyId(parentId.getName()));

            lockPackage.getDependencies().forEach(childPackage -> {
                if (childPackage.getVersion().isPresent()) {
                    NameVersionDependencyId childId = new NameVersionDependencyId(childPackage.getName(), childPackage.getVersion().get());
                    graph.addChildWithParent(childId, parentId);
                } else {
                    NameDependencyId childId = new NameDependencyId(childPackage.getName());
                    graph.addChildWithParent(childId, parentId);
                }
            });
        });
        
        return RootPruningGraphUtil.prune(graph.build());
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
