package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LicenseManifestFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO this needs refactoring; also, compare to MB's search
    public Optional<File> find(File sourceDir, String targetImageName) {
//        if (StringUtils.isNotBlank(givenLicenseManifestFilePath)) {
//            File licenseManifestFile = new File (givenLicenseManifestFilePath);
//            if (licenseManifestFile.canRead()) {
//                logger.debug("Found license.manifest file at given path: {}", givenLicenseManifestFilePath);
//                return Optional.of(licenseManifestFile);
//            }
//        }
        // TODO might need to be more flexible?
        // TODO might need to determine which architecture to look for?
        Optional<File> licenseFile;
        try {
            File buildDir = new File(sourceDir, "build");
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
                    return Optional.of(licenseFileCandidate);
                }
            }
        } catch (Exception e) {

        }
        logger.error("Unable to find license.manifest file for target image {}", targetImageName);
        return Optional.empty();
    }
}
