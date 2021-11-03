/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockExtractor {
    private final Gson gson;
    private final PnpmLockYamlParser pnpmLockYamlParser;

    public PnpmLockExtractor(Gson gson, PnpmLockYamlParser pnpmLockYamlParser) {
        this.gson = gson;
        this.pnpmLockYamlParser = pnpmLockYamlParser;
    }

    public Extraction extract(File yarnLockYamlFile, @Nullable File packageJsonFile, List<DependencyType> dependencyTypes) {
        try {
            Optional<NameVersion> nameVersion = parseNameVersionFromPackageJson(packageJsonFile);
            CodeLocation codeLocation = pnpmLockYamlParser.parse(yarnLockYamlFile, dependencyTypes, nameVersion.orElse(null));
            return new Extraction.Builder().success(codeLocation)
                       .nameVersionIfPresent(nameVersion)
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> parseNameVersionFromPackageJson(File packageJsonFile) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
        PackageJson packageJson = gson.fromJson(packageJsonText, PackageJson.class);
        if (packageJson != null && packageJson.name != null && packageJson.version != null) {
            return Optional.of(new NameVersion(packageJson.name, packageJson.version));
        }
        return Optional.empty();
    }
}
