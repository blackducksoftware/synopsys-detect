package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.exception.IntegrationException;

public class LicenseManifestFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FileFinder fileFinder;

    public LicenseManifestFinder(final FileFinder fileFinder) {
        this.fileFinder = fileFinder;
    }

    public File find(File buildDir, String targetImageName, boolean followSymLinks, int searchDepth) throws IntegrationException {
        try {
            File licensesDir = findLicensesDir(buildDir, followSymLinks, searchDepth);
            logger.debug("Checking licenses dir {} for license.manifest for {}", licensesDir.getAbsolutePath(), targetImageName);
            List<File> licensesDirContents = Arrays.asList(licensesDir.listFiles());
            Optional<File> latestLicenseManifestFile = findMostRecentLicenseManifestFileForTarget(targetImageName, licensesDirContents, followSymLinks);
            if (latestLicenseManifestFile.isPresent() && latestLicenseManifestFile.get().canRead()) {
                logger.debug("Found readable license.manifest file: {}", latestLicenseManifestFile.get().getAbsolutePath());
                return latestLicenseManifestFile.get();
            }
        } catch (Exception e) {
            logger.debug(String.format("Error finding license.manifest file for target image %s", targetImageName), e);
        }
        throw new IntegrationException(String.format("Unable to find license.manifest file for target image %s", targetImageName));
    }

    private File findLicensesDir(File buildDir, boolean followSymLinks, int searchDepth) throws IntegrationException {
        File defaultLicensesDir = new File(buildDir, "tmp/deploy/licenses");
        if (defaultLicensesDir.isDirectory()) {
            return defaultLicensesDir;
        }
        logger.trace("Licenses dir {} not found; searching build directory", defaultLicensesDir.getAbsolutePath());
        List<File> licensesDirs = fileFinder.findFiles(buildDir, f -> f.getName().equals("licenses") && f.isDirectory(), followSymLinks, searchDepth);
        logger.trace("Found {} licenses directories in {}", licensesDirs.size(), buildDir.getAbsolutePath());
        if (licensesDirs.size() == 0) {
            throw new IntegrationException(String.format("Unable to find 'licenses' directory in %s", buildDir.getAbsolutePath()));
        }
        List<File> deployLicensesDirs = licensesDirs.stream()
            .filter(f -> f.getParentFile().getName().equals("deploy"))
            .collect(Collectors.toList());
        logger.debug("Found {} 'deploy/licenses' directories", deployLicensesDirs.size());
        if (deployLicensesDirs.size() == 0) {
            logger.debug("Using licenses directory {}", licensesDirs.get(0));
            return licensesDirs.get(0);
        }
        logger.debug("Using licenses directory {}", deployLicensesDirs.get(0));
        return deployLicensesDirs.get(0);
    }

    private Optional<File> findMostRecentLicenseManifestFileForTarget(final String targetImageName, final List<File> licensesDirContents, boolean followSymLinks) {
        File latestLicenseManifestFile = null;
        long latestLicenseManifestFileTime = 0;
        for (File licensesDirSubDir : licensesDirContents) {
            if (licensesDirSubDir.getName().startsWith(targetImageName) && (followSymLinks || !FileUtils.isSymlink(licensesDirSubDir))) {
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
        return Optional.ofNullable(latestLicenseManifestFile);
    }
}
