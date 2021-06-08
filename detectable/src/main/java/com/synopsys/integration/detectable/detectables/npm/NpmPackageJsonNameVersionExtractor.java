/*
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
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.util.NameVersion;

public class NpmPackageJsonNameVersionExtractor {
    private final Gson gson;

    public NpmPackageJsonNameVersionExtractor(Gson gson) {
        this.gson = gson;
    }

    public NameVersion extract(File packageJson) {
        try {
            String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            PackageJson packageJsonModel = gson.fromJson(packageJsonText, PackageJson.class);
            return new NameVersion(packageJsonModel.name, packageJsonModel.version);
        } catch (IOException e) {
            return new NameVersion("", "");
        }
    }
}
