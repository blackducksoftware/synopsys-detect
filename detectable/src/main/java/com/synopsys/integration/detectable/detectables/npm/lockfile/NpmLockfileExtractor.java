package com.synopsys.integration.detectable.detectables.npm.lockfile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfilePackager;
import com.synopsys.integration.detectable.detectables.npm.lockfile.result.NpmPackagerResult;
import com.synopsys.integration.detectable.extraction.Extraction;

public class NpmLockfileExtractor {
    private final NpmLockfilePackager npmLockfilePackager;

    public NpmLockfileExtractor(NpmLockfilePackager npmLockfilePackager) {
        this.npmLockfilePackager = npmLockfilePackager;
    }

    /*
    packageJson is optional
     */
    public Extraction extract(File lockfile, File packageJson) {
        try {
            String lockText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            String packageText = null;
            if (packageJson != null) {
                packageText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            }

            NpmPackagerResult result = npmLockfilePackager.parseAndTransform(packageText, lockText);

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
