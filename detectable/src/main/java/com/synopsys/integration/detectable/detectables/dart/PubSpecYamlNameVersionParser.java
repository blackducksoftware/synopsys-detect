/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.dart;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.util.NameVersion;

public class PubSpecYamlNameVersionParser {
    private static final String NAME_KEY = "name:";
    private static final String VERSION_KEY = "version:";

    public Optional<NameVersion> parseNameVersion(File pubSpecYamlFile) throws IOException {
        if (pubSpecYamlFile == null) {
            return Optional.empty();
        }

        List<String> pubSpecYamlLines = Files.readAllLines(pubSpecYamlFile.toPath(), StandardCharsets.UTF_8);
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
