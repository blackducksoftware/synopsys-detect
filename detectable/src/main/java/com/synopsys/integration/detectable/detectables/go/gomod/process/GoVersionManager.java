/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class GoVersionManager {
    private static final String INCOMPATIBLE_SUFFIX = "+incompatible";

    private final List<GoListAllData> allModules;

    public GoVersionManager(List<GoListAllData> allModules) {
        this.allModules = allModules;
    }

    public Optional<String> getVersionForModule(String moduleName) {
        return allModules.stream()
            .filter(module -> moduleName.equals(module.getPath()))
            .map(module -> Optional.ofNullable(module.getReplace())
                .map(ReplaceData::getVersion)
                .orElse(module.getVersion())
            )
            .filter(Objects::nonNull)
            .map(version -> {
                version = handleGitHash(version);
                return removeIncompatibleSuffix(version);

            })
            .findFirst();
    }

    private String handleGitHash(String version) {
        if (version.contains("-")) { //The KB only supports the git hash, unfortunately we must strip out the rest. This gets just the commit has from a go.mod psuedo version.
            String[] versionPieces = version.split("-");
            return versionPieces[versionPieces.length - 1];
        }
        return version;
    }

    // https://golang.org/ref/mod#incompatible-versions
    private String removeIncompatibleSuffix(String version) {
        if (version.endsWith(INCOMPATIBLE_SUFFIX)) {
            // Trim incompatible suffix so that KB can match component
            version = version.substring(0, version.length() - INCOMPATIBLE_SUFFIX.length());
        }
        return version;
    }
}
