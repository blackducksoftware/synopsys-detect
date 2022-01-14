package com.synopsys.integration.detectable.detectables.cargo;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.util.TomlFileUtils;
import com.synopsys.integration.detectable.detectables.cargo.model.CargoLock;
import com.synopsys.integration.detectable.detectables.cargo.parse.CargoLockTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class CargoExtractor {

    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";
    private static final String PACKAGE_KEY = "package";

    private final CargoLockTransformer cargoLockTransformer;

    public CargoExtractor(CargoLockTransformer cargoLockTransformer) {
        this.cargoLockTransformer = cargoLockTransformer;
    }

    public Extraction extract(File cargoLockFile, @Nullable File cargoToml) {
        try {
            CargoLock cargoLock = new Toml().read(cargoLockFile).to(CargoLock.class);
            DependencyGraph graph = cargoLockTransformer.toDependencyGraph(cargoLock)
                .orElse(new MutableMapDependencyGraph());

            Optional<NameVersion> cargoNameVersion = extractNameVersionFromCargoToml(cargoToml);
            CodeLocation codeLocation = new CodeLocation(graph); //TODO: Consider for 8.0.0 providing an external ID.

            return new Extraction.Builder()
                .success(codeLocation)
                .nameVersionIfPresent(cargoNameVersion)
                .build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    //TODO: This could go in a parser and be tested. (Or just in a parser).
    //TODO: Consider making a model object.
    private Optional<NameVersion> extractNameVersionFromCargoToml(@Nullable File cargoToml) throws IOException {
        if (cargoToml != null) {
            TomlParseResult cargoTomlObject = TomlFileUtils.parseFile(cargoToml);
            if (cargoTomlObject.get(PACKAGE_KEY) != null) {
                TomlTable cargoTomlPackageInfo = cargoTomlObject.getTable(PACKAGE_KEY);
                if (cargoTomlPackageInfo.get(NAME_KEY) != null && cargoTomlPackageInfo.get(VERSION_KEY) != null) {
                    return Optional.of(new NameVersion(cargoTomlPackageInfo.getString(NAME_KEY), cargoTomlPackageInfo.getString(VERSION_KEY)));
                }
            }
        }
        return Optional.empty();
    }
}
