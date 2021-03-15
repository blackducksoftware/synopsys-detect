/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmPackageJsonDiscoverer {
    private final Gson gson;

    public NpmPackageJsonDiscoverer(final Gson gson) {
        this.gson = gson;
    }

    public Discovery discover(final File packageJson) {
        try {
            final String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            final PackageJson packageJsonModel = gson.fromJson(packageJsonText, PackageJson.class);
            return new Discovery.Builder().success(packageJsonModel.name, packageJsonModel.version).build();
        } catch (final IOException e) {
            return new Discovery.Builder().exception(e).build();
        }
    }
}
