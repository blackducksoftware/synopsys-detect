package com.synopsys.integration.detectable.detectables.cargo.transform;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectables.cargo.data.CargoLockPackageData;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLockPackage;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoDependencyLineParser;
import com.synopsys.integration.detectable.util.NameOptionalVersion;
import com.synopsys.integration.util.NameVersion;

public class CargoLockPackageDataTransformer {
    private final CargoDependencyLineParser cargoDependencyLineParser;

    public CargoLockPackageDataTransformer(CargoDependencyLineParser cargoDependencyLineParser) {
        this.cargoDependencyLineParser = cargoDependencyLineParser;
    }

    public CargoLockPackage transform(CargoLockPackageData cargoLockPackageData) {
        String packageName = cargoLockPackageData.getName().orElse("");
        String packageVersion = cargoLockPackageData.getVersion().orElse(null);
        NameVersion nameOptionalVersion = new NameVersion(packageName, packageVersion);

        List<NameOptionalVersion> dependencies = cargoLockPackageData.getDependencies()
            .map((List<String> dependencyLines) -> dependencyLines.stream()
                .map(cargoDependencyLineParser::parseDependencyName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()))
            .orElse(Collections.emptyList());

        return new CargoLockPackage(nameOptionalVersion, dependencies);
    }
}
