package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.Optional;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlData;
import com.synopsys.integration.detectable.detectables.cargo.data.CargoTomlPackageData;
import com.synopsys.integration.util.NameVersion;

public class CargoTomlDataTransformer {
    public Optional<NameVersion> findProjectNameVersion(CargoTomlData cargoTomlData) {
        return cargoTomlData.getCargoTomlPackage()
            .map(CargoTomlPackageData::getName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(extractedName -> {
                String extractedVersion = cargoTomlData.getCargoTomlPackage()
                    .map(CargoTomlPackageData::getVersion)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .orElse(null);
                return new NameVersion(extractedName, extractedVersion);
            });
    }
}
