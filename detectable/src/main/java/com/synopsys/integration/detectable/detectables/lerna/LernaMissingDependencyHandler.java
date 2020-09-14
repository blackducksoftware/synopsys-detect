package com.synopsys.integration.detectable.detectables.lerna;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnTransformer;

public class LernaMissingDependencyHandler {
    private final List<LernaPackage> lernaPackages;

    public LernaMissingDependencyHandler(List<LernaPackage> lernaPackages) {
        this.lernaPackages = lernaPackages;
    }

    public void missingNpmDependencyHandler(Logger logger, NpmRequires missingDependency) {
        boolean isLernaPackage = lernaPackages.stream()
                                     .anyMatch(lernaPackage -> lernaPackage.getName().equalsIgnoreCase(missingDependency.getName()));
        if (!isLernaPackage) {
            NpmLockfilePackager.handleMissingDependency(logger, missingDependency);
        }
    }

    public ExternalId missingYarnDependencyHandler(Logger logger, ExternalIdFactory externalIdFactory, DependencyId dependencyId, LazyExternalIdDependencyGraphBuilder.LazyDependencyInfo lazyDependencyInfo, String yarnLockFilePath) {
        Optional<LernaPackage> foundLernaPackage = lernaPackages.stream()
                                                       .filter(lernaPackage -> dependencyId.toString().toLowerCase().contains(lernaPackage.getName().toLowerCase()))
                                                       .findAny();
        if (foundLernaPackage.isPresent()) {
            return externalIdFactory.createNameVersionExternalId(Forge.NPMJS, foundLernaPackage.get().getName(), foundLernaPackage.get().getVersion());
        }

        return YarnTransformer.handleMissingExternalIds(logger, externalIdFactory, dependencyId, lazyDependencyInfo, yarnLockFilePath);
    }
}
