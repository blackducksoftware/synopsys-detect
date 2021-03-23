/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NullSafePackageJson {
    private final PackageJson rawPackageJson;

    public NullSafePackageJson(PackageJson rawPackageJson) {
        this.rawPackageJson = rawPackageJson;
    }

    public Optional<String> getName() {
        if (rawPackageJson.name == null) {
            return Optional.empty();
        }
        return Optional.of(rawPackageJson.name);
    }

    public Optional<String> getVersion() {
        if (rawPackageJson.version == null) {
            return Optional.empty();
        }
        return Optional.of(rawPackageJson.version);
    }

    public Map<String, String> getDependencies() {
        if (rawPackageJson.dependencies == null) {
            return new HashMap<>();
        }
        return rawPackageJson.dependencies;
    }

    public Map<String, String> getDevDependencies() {
        if (rawPackageJson.devDependencies == null) {
            return new HashMap<>();
        }
        return rawPackageJson.devDependencies;
    }
}
