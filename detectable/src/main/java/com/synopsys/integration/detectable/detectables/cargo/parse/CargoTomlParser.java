package com.synopsys.integration.detectable.detectables.cargo.parse;

import java.util.Optional;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.util.NameVersion;

public class CargoTomlParser {
    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";
    private static final String PACKAGE_KEY = "package";

    public Optional<NameVersion> parseNameVersionFromCargoToml(String tomlFileContents) {
        TomlParseResult cargoTomlObject = Toml.parse(tomlFileContents);
        if (cargoTomlObject.contains(PACKAGE_KEY)) {
            return Optional.ofNullable(cargoTomlObject.getTable(PACKAGE_KEY))
                .filter(info -> info.contains(NAME_KEY))
                .map(info -> new NameVersion(info.getString(NAME_KEY), info.getString(VERSION_KEY)));
        }
        return Optional.empty();
    }

}
