package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.exception.IntegrationException;

public class LicenseManifestFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO compare to MB's search, and refactor
    public File find(File sourceDir, File buildDir, String targetImageName, @Nullable String givenLicenseManifestFilePath) throws IntegrationException {
        if (StringUtils.isNotBlank(givenLicenseManifestFilePath)) {
            return findFile(sourceDir, givenLicenseManifestFilePath);
        }
        try {
            File tmpDir = new File(buildDir, "tmp");
            File deployDir = new File(tmpDir, "deploy");
            File licensesDir = new File(deployDir, "licenses");
            logger.debug("Checking licenses dir {} for license.manifest for {}", licensesDir.getAbsolutePath(), targetImageName);
            List<File> licensesDirContents = Arrays.asList(licensesDir.listFiles());
            Optional<File> targetImageLicenseDir = licensesDirContents.stream().filter(f -> f.getName().startsWith(targetImageName)).findFirst();
            if (targetImageLicenseDir.isPresent()) {
                logger.debug("Found target image license dir: {}", targetImageLicenseDir.get().getAbsolutePath());
                File licenseFileCandidate = new File(targetImageLicenseDir.get(), "license.manifest");
                if (licenseFileCandidate.canRead()) {
                    logger.debug("Found license.manifest file: {}", licenseFileCandidate.getAbsolutePath());
                    return licenseFileCandidate;
                }
            }
        } catch (Exception e) {
            logger.debug(String.format("Error finding license.manifest file for target image %s", targetImageName), e);
        }
        throw new IntegrationException(String.format("Unable to find license.manifest file for target image %s", targetImageName));
    }

    private File findFile(File sourceDir, String givenPath) throws IntegrationException {
        File givenFile = new File(givenPath);
        if (givenFile.canRead()) {
            return givenFile;
        }
        // TODO this enables the battery test to work, but really shouldn't be here
        File sourceDirFile = new File(sourceDir, givenPath);
        if (sourceDirFile.canRead()) {
            return sourceDirFile;
        }
        throw new IntegrationException(String.format("Unable to find license.manifest file at given path %s", givenPath));
    }
}
