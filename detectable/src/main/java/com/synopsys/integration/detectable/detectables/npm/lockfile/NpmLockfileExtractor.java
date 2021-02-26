/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;

public class NpmLockfileExtractor {
    private final NpmLockfilePackager npmLockfileParser;

    public NpmLockfileExtractor(NpmLockfilePackager npmLockfileParser) {
        this.npmLockfileParser = npmLockfileParser;
    }

    /*
    packageJson is optional
     */
    public Extraction extract(File lockfile, File packageJson, boolean includeDevDependencies) {
        try {
            String lockText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            String packageText = null;
            if (packageJson != null) {
                packageText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            }

            NpmParseResult result = npmLockfileParser.parse(packageText, lockText, includeDevDependencies);

            return new Extraction.Builder()
                       .success(result.getCodeLocation())
                       .projectName(result.getProjectName())
                       .projectVersion(result.getProjectVersion())
                       .build();

        } catch (IOException e) {
            return new Extraction.Builder()
                       .exception(e)
                       .build();
        }
    }
}
