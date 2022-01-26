package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockDataTransformer;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockTransformer;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoTomlTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.util.RootPruningGraphUtil;
import com.synopsys.integration.util.NameVersion;

public class CargoExtractor {
    private final CargoLockDataTransformer cargoLockDataTransformer;
    private final CargoTomlTransformer cargoTomlTransformer;
    private final CargoLockTransformer cargoLockTransformer;

    public CargoExtractor(CargoLockDataTransformer cargoLockDataTransformer, CargoTomlTransformer cargoTomlTransformer, CargoLockTransformer cargoLockTransformer) {
        this.cargoLockDataTransformer = cargoLockDataTransformer;
        this.cargoTomlTransformer = cargoTomlTransformer;
        this.cargoLockTransformer = cargoLockTransformer;
    }

    public Extraction extract(File cargoLockFile, @Nullable File cargoTomlFile) throws IOException, RootPruningGraphUtil.CycleDetectedException, DetectableException {
        CargoLock cargoLock = new Toml().read(cargoLockFile).to(CargoLock.class);
        List<CargoLockPackage> packages = cargoLock.getPackages()
            .orElse(new ArrayList<>()).stream()
            .map(cargoLockDataTransformer::transform)
            .collect(Collectors.toList());
        DependencyGraph graph = null;
        try {
            graph = cargoLockTransformer.transformToGraph(packages);
        } catch (MissingExternalIdException e) {
            // TODO: Wrap then throw. The 'detector' subproject doesn't have access to MissingExternalIdException.
            throw new DetectableException(e);
        }

        Optional<NameVersion> projectNameVersion = Optional.empty();
        if (cargoTomlFile != null) {
            CargoToml cargoToml = new Toml().read(cargoTomlFile).to(CargoToml.class);
            projectNameVersion = cargoTomlTransformer.findProjectNameVersion(cargoToml);
        }

        CodeLocation codeLocation = new CodeLocation(graph); //TODO: Consider for 8.0.0 providing an external ID.

        return new Extraction.Builder()
            .success(codeLocation)
            .nameVersionIfPresent(projectNameVersion)
            .build();
    }
}
