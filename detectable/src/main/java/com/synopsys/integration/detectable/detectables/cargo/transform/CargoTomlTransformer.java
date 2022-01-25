package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoToml;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackage;
import com.synopsys.integration.util.NameVersion;

public class CargoTomlTransformer {
    public Optional<NameVersion> findProjectNameVersion(CargoToml cargoToml) {
        return cargoToml.getCargoTomlPackage()
            .map(CargoTomlPackage::getName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(extractedName -> {
                String extractedVersion = cargoToml.getCargoTomlPackage()
                    .map(CargoTomlPackage::getVersion)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .orElse(null);
                return new NameVersion(extractedName, extractedVersion);
            });
    }
}
