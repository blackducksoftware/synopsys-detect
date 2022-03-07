package com.synopsys.integration.detectable.detectables.bitbake.collect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.data.BitbakeEnvironment;
import com.synopsys.integration.exception.IntegrationException;

public class BuildFileFinder {
    private static final String TASK_DEPENDS_FILE_NAME = "task-depends.dot";
    private static final String LICENSE_MANIFEST_FILENAME = "license.manifest";
    private static final String LICENSES_DIR_NAME = "licenses";
    private static final String LICENSES_DIR_DEFAULT_PATH_REL_TO_BUILD_DIR = "tmp/deploy/" + LICENSES_DIR_NAME;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final boolean followSymLinks;
    private final int searchDepth;

    public BuildFileFinder(FileFinder fileFinder, boolean followSymLinks, int searchDepth) {
        this.fileFinder = fileFinder;
        this.followSymLinks = followSymLinks;
        this.searchDepth = searchDepth;
    }

    public File findTaskDependsFile(File sourceDir, File buildDir) throws IntegrationException {
        File taskDependsDotFile = fileFinder.findFile(buildDir, TASK_DEPENDS_FILE_NAME, followSymLinks, searchDepth);
        if (taskDependsDotFile == null) {
            logger.warn("Did not find {} in build dir {}; trying source dir", TASK_DEPENDS_FILE_NAME, buildDir.getAbsolutePath());
            taskDependsDotFile = fileFinder.findFile(sourceDir, TASK_DEPENDS_FILE_NAME, followSymLinks, searchDepth);
        }
        if (taskDependsDotFile == null) {
            throw new IntegrationException(String.format("Failed to find %s in either %s or %s",
                TASK_DEPENDS_FILE_NAME, buildDir.getAbsolutePath(), sourceDir.getAbsolutePath()
            ));
        }
        return taskDependsDotFile;
    }

    public Optional<File> findLicenseManifestFile(File buildDir, String targetImageName, BitbakeEnvironment bitbakeEnvironment) {
        try {
            String machineArch = bitbakeEnvironment.getMachineArch().orElse(null);
            File licensesDir = findLicensesDir(buildDir, bitbakeEnvironment.getLicensesDirPath().orElse(null));
            logger.debug("Checking licenses dir {} for license.manifest for {}", licensesDir.getAbsolutePath(), targetImageName);
            List<File> licensesDirContents = generateListOfFiles(licensesDir);
            Optional<File> architectureSpecificManifestFile = findManifestFileForTargetArchitecture(targetImageName, machineArch, licensesDirContents);
            if (architectureSpecificManifestFile.isPresent()) {
                return architectureSpecificManifestFile;
            }
            logger.debug("Did not find a license.manifest for architecture {}; Will look for the most recent license.manifest file.", machineArch);
            Optional<File> latestLicenseManifestFile = findMostRecentLicenseManifestFileForTarget(targetImageName, licensesDirContents);
            if (latestLicenseManifestFile.isPresent()) {
                logger.debug("Found most recent license.manifest file: {}", latestLicenseManifestFile.get().getAbsolutePath());
                return latestLicenseManifestFile;
            }
        } catch (Exception e) {
            logger.debug(String.format("Error finding license.manifest file for target image %s", targetImageName), e);
        }
        logger.debug("Unable to find license.manifest file for target image {}", targetImageName);
        return Optional.empty();
    }

    @NotNull
    private List<File> generateListOfFiles(File licensesDir) {
        File[] licensesDirContentsArray = licensesDir.listFiles();
        if (licensesDirContentsArray == null) {
            return new ArrayList<>(0);
        }
        return Arrays.asList(licensesDirContentsArray);
    }

    private File findLicensesDir(File buildDir, @Nullable String licensesDirPath) throws IntegrationException {
        if (licensesDirPath != null) {
            File envSpecifiedLicensesDir = new File(licensesDirPath);
            if (envSpecifiedLicensesDir.isDirectory()) {
                logger.debug("Found licenses directory discovered using the bitbake environment: {}", envSpecifiedLicensesDir.getAbsolutePath());
                return envSpecifiedLicensesDir;
            }
        }
        File defaultLicensesDir = new File(buildDir, LICENSES_DIR_DEFAULT_PATH_REL_TO_BUILD_DIR);
        if (defaultLicensesDir.isDirectory()) {
            logger.debug("Found licenses directory in the default location: {}", defaultLicensesDir.getAbsolutePath());
            return defaultLicensesDir;
        }
        logger.trace("Licenses dir {} not found; searching build directory", defaultLicensesDir.getAbsolutePath());
        List<File> licensesDirs = fileFinder.findFiles(buildDir, f -> f.getName().equals(LICENSES_DIR_NAME) && f.isDirectory(), followSymLinks, searchDepth);
        logger.trace("Found {} licenses directories in {}", licensesDirs.size(), buildDir.getAbsolutePath());
        if (licensesDirs.isEmpty()) {
            throw new IntegrationException(String.format("Unable to find 'licenses' directory in %s", buildDir.getAbsolutePath()));
        }
        logger.debug("Using licenses directory {}", licensesDirs.get(0));
        return licensesDirs.get(0);
    }

    private Optional<File> findManifestFileForTargetArchitecture(String targetImageName, @Nullable String architecture, List<File> licensesDirContents) {
        if (architecture == null) {
            return Optional.empty();
        }
        String targetDirPrefix = targetImageName + "-" + architecture;
        for (File licensesDirSubDir : licensesDirContents) {
            if (!licensesDirSubDir.isDirectory()) {
                continue;
            }
            if (licensesDirSubDir.getName().startsWith(targetDirPrefix) && (followSymLinks || !FileUtils.isSymlink(licensesDirSubDir))) {
                File thisLicenseManifestFile = new File(licensesDirSubDir, LICENSE_MANIFEST_FILENAME);
                if (thisLicenseManifestFile.exists()) {
                    logger.debug("Found license.manifest for current architecture ({}): {}", architecture, thisLicenseManifestFile.getAbsolutePath());
                    return Optional.of(thisLicenseManifestFile);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<File> findMostRecentLicenseManifestFileForTarget(String targetImageName, List<File> licensesDirContents) {
        File latestLicenseManifestFile = null;
        long latestLicenseManifestFileTime = 0;
        for (File licensesDirSubDir : licensesDirContents) {
            if (licensesDirSubDir.getName().startsWith(targetImageName) && (followSymLinks || !FileUtils.isSymlink(licensesDirSubDir))) {
                File thisLicenseManifestFile = new File(licensesDirSubDir, LICENSE_MANIFEST_FILENAME);
                if ((thisLicenseManifestFile.exists()) && ((latestLicenseManifestFileTime == 0) || (thisLicenseManifestFile.lastModified() > latestLicenseManifestFileTime))) {
                    // Newest found so far
                    latestLicenseManifestFileTime = thisLicenseManifestFile.lastModified();
                    latestLicenseManifestFile = thisLicenseManifestFile;
                }
            }
        }
        return Optional.ofNullable(latestLicenseManifestFile);
    }
}
