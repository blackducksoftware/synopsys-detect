package com.synopsys.integration.detectable.detectables.dart;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.util.NameVersion;

public class PubSpecYamlNameVersionParser {
    private static final String NAME_KEY = "name:";
    private static final String VERSION_KEY = "version:";

    public Optional<NameVersion> parseNameVersion(List<String> pubSpecYamlLines) {
        String name = null;
        String version = null;
        for (String line : pubSpecYamlLines) {
            if (line.trim().startsWith(NAME_KEY)) {
                name = line.trim().split(" ")[1];
            } else if (line.trim().startsWith(VERSION_KEY)) {
                version = line.trim().split(" ")[1];
            }
        }
        if (name == null && version == null) {
            return Optional.empty();
        }
        return Optional.of(new NameVersion(name, version));
    }
}
