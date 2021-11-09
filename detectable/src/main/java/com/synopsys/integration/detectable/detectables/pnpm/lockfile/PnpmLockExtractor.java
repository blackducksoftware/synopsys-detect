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
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.NullSafePackageJson;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PnpmLockExtractor {
    private final PnpmLockYamlParser pnpmLockYamlParser;
    private final PackageJsonFiles packageJsonFiles;

    public PnpmLockExtractor(PnpmLockYamlParser pnpmLockYamlParser, PackageJsonFiles packageJsonFiles) {
        this.pnpmLockYamlParser = pnpmLockYamlParser;
        this.packageJsonFiles = packageJsonFiles;
    }

    public Extraction extract(File yarnLockYamlFile, @Nullable File packageJsonFile, List<DependencyType> dependencyTypes, PnpmLinkedPackageResolver linkedPackageResolver) {
        try {
            Optional<NameVersion> nameVersion = parseNameVersionFromPackageJson(packageJsonFile);
            List<CodeLocation> codeLocations = pnpmLockYamlParser.parse(yarnLockYamlFile, dependencyTypes, nameVersion.orElse(null), linkedPackageResolver);
            return new Extraction.Builder().success(codeLocations)
                       .nameVersionIfPresent(nameVersion)
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> parseNameVersionFromPackageJson(File packageJsonFile) throws IOException {
        NullSafePackageJson nullSafePackageJson = packageJsonFiles.read(packageJsonFile);
        if (nullSafePackageJson.getName().isPresent() && nullSafePackageJson.getVersion().isPresent()) {
            return Optional.of(new NameVersion(nullSafePackageJson.getNameString(), nullSafePackageJson.getVersionString()));
        }
        return Optional.empty();
    }
}
