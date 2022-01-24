package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoLockTransformer;
import com.synopsys.integration.detectable.detectables.cargo.transform.CargoTomlTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class CargoExtractor {
    private final CargoTomlTransformer cargoTomlTransformer;
    private final CargoLockTransformer cargoLockTransformer;

    public CargoExtractor(CargoTomlTransformer cargoTomlTransformer, CargoLockTransformer cargoLockTransformer) {
        this.cargoTomlTransformer = cargoTomlTransformer;
        this.cargoLockTransformer = cargoLockTransformer;
    }

    public Extraction extract(File cargoLockFile, @Nullable File cargoTomlFile) throws IOException {
        CargoLock cargoLock = new Toml().read(cargoLockFile).to(CargoLock.class);
        DependencyGraph graph = cargoLockTransformer.toDependencyGraph(cargoLock)
            .orElse(new MutableMapDependencyGraph());

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
