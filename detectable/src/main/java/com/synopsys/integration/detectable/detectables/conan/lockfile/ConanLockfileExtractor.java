/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.conan.lockfile;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.ConanLockfileParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class ConanLockfileExtractor {
    private final ConanLockfileParser conanLockfileParser;

    public ConanLockfileExtractor(ConanLockfileParser conanLockfileParser) {
        this.conanLockfileParser = conanLockfileParser;
    }

    public Extraction extract(File lockfile, ConanLockfileExtractorOptions conanLockfileExtractorOptions) {
        try {
            String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            ConanDetectableResult result = conanLockfileParser.generateCodeLocationFromConanLockfileContents(
                conanLockfileContents,
                conanLockfileExtractorOptions.shouldIncludeDevDependencies(),
                conanLockfileExtractorOptions.preferLongFormExternalIds());
            return new Extraction.Builder()
                       .success(result.getCodeLocation())
                       .projectName(result.getProjectName())
                       .projectVersion(result.getProjectVersion())
                       .build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}