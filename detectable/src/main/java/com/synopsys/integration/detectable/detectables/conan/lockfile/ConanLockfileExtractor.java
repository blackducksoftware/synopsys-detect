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

    public Extraction extract(File lockfile) {
        try {
            String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            ConanDetectableResult result = conanLockfileParser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);
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