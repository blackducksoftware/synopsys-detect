package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.exception.IntegrationException;

public class LicenseManifestFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public File find(File buildDir, String targetImageName) throws IntegrationException {
        try {
            File licensesDir = new File(buildDir, "tmp/deploy/licenses");
            logger.debug("Checking licenses dir {} for license.manifest for {}", licensesDir.getAbsolutePath(), targetImageName);
            List<File> licensesDirContents = Arrays.asList(licensesDir.listFiles());
            Optional<File> latestLicenseManifestFile = findMostRecentLicenseManifestFileForTarget(targetImageName, licensesDirContents);
            if (latestLicenseManifestFile.isPresent()) {
                logger.trace("Found target image license.manifest file: {}", latestLicenseManifestFile.get().getAbsolutePath());
                if (latestLicenseManifestFile.get().canRead()) {
                    logger.debug("Found readable license.manifest file: {}", latestLicenseManifestFile.get().getAbsolutePath());
                    return latestLicenseManifestFile.get();
                }
            }
        } catch (Exception e) {
            logger.debug(String.format("Error finding license.manifest file for target image %s", targetImageName), e);
        }
        throw new IntegrationException(String.format("Unable to find license.manifest file for target image %s", targetImageName));
    }

    private Optional<File> findMostRecentLicenseManifestFileForTarget(final String targetImageName, final List<File> licensesDirContents) {
        // TODO use a stream?
        File latestLicenseManifestFile = null;
        long latestLicenseManifestFileTime = 0;
        for (File licensesDirSubDir : licensesDirContents) {
            if (licensesDirSubDir.getName().startsWith(targetImageName)) {
                if (!FileUtils.isSymlink(licensesDirSubDir)) {
                    File thisLicenseManifestFile = new File(licensesDirSubDir, "license.manifest");
                    if (thisLicenseManifestFile.exists()) {
                        if ((latestLicenseManifestFileTime == 0) || (thisLicenseManifestFile.lastModified() > latestLicenseManifestFileTime)) {
                            latestLicenseManifestFileTime = thisLicenseManifestFile.lastModified();
                            latestLicenseManifestFile = thisLicenseManifestFile;
                            logger.trace("Latest so far: {}", latestLicenseManifestFile.getAbsolutePath());
                        } else {
                            logger.trace("Not the latest: {}", thisLicenseManifestFile.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return Optional.ofNullable(latestLicenseManifestFile);
    }
}
