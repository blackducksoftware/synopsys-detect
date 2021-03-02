/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.clang.linux;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO Perhaps this should move into a lib like integration-common to be used
// here and by hub-imageinspector-lib
public class LinuxDistro {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<String> extractLinuxDistroNameFromEtcDir(File etcDir) {
        logger.trace(String.format("/etc directory: %s", etcDir.getAbsolutePath()));
        if (etcDir.listFiles().length == 0) {
            logger.warn(String.format("Could not determine the Operating System because the /etc dir (%s) is empty", etcDir.getAbsolutePath()));
        }
        return extractLinuxDistroNameFromFiles(etcDir.listFiles());
    }

    private Optional<String> extractLinuxDistroNameFromFiles(File[] etcFiles) {
        for (File etcFile : etcFiles) {
            if (isLinuxDistroFile(etcFile)) {
                Optional<String> distroAccordingToThisFile = getLinxDistroName(etcFile);
                if (distroAccordingToThisFile.isPresent()) {
                    return distroAccordingToThisFile;
                }
            }
        }
        return Optional.empty();
    }

    private boolean isLinuxDistroFile(File candidate) {
        if ("lsb-release".equals(candidate.getName())) {
            return true;
        }
        if ("os-release".equals(candidate.getName())) {
            return true;
        }
        return "redhat-release".equals(candidate.getName());
    }

    private Optional<String> getLinxDistroName(File etcDirFile) {
        try {
            if ("redhat-release".equals(etcDirFile.getName())) {
                return getLinuxDistroNameFromRedHatReleaseFile(etcDirFile);
            } else {
                return getLinuxDistroNameFromStandardReleaseFile(etcDirFile);
            }
        } catch (Exception e) {
            logger.warn(String.format("Error reading or parsing Linux distribution-identifying file: %s: %s", etcDirFile, e.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<String> getLinuxDistroNameFromStandardReleaseFile(File etcDirFile) {
        String linePrefix = null;
        if ("lsb-release".equals(etcDirFile.getName())) {
            logger.trace("Found lsb-release");
            linePrefix = "DISTRIB_ID=";
        } else if ("os-release".equals(etcDirFile.getName())) {
            logger.trace("Found os-release");
            linePrefix = "ID=";
        } else {
            logger.warn(String.format("File %s is not a Linux distribution-identifying file", etcDirFile.getAbsolutePath()));
            return Optional.empty();
        }
        try {
            List<String> lines = null;
            lines = FileUtils.readLines(etcDirFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith(linePrefix)) {
                    String[] parts = line.split("=");
                    String distroName = parts[1].replace("\"", "").toLowerCase();
                    logger.debug(String.format("Found target image Linux distro name '%s' in file %s", distroName, etcDirFile.getAbsolutePath()));
                    return Optional.of(distroName);
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", etcDirFile.getAbsolutePath()));
            return Optional.empty();
        }
        logger.warn(String.format("Did not find value for %s in %s", linePrefix, etcDirFile.getAbsolutePath()));
        return Optional.empty();
    }

    private Optional<String> getLinuxDistroNameFromRedHatReleaseFile(File etcDirFile) {
        logger.trace("Found redhat-release");
        try {
            List<String> lines = null;
            lines = FileUtils.readLines(etcDirFile, StandardCharsets.UTF_8);
            if (!lines.isEmpty()) {
                String line = lines.get(0);
                if (line.startsWith("Red Hat")) {
                    logger.trace("Contents of redhat-release indicate RHEL");
                    return Optional.of("rhel");
                }
                if (line.startsWith("CentOS")) {
                    logger.trace("Contents of redhat-release indicate CentOS");
                    return Optional.of("centos");
                }
                if (line.startsWith("Fedora")) {
                    logger.trace("Contents of redhat-release indicate Fedora");
                    return Optional.of("fedora");
                }
                logger.warn(String.format("Found redhat-release file %s but don't understand the contents: '%s'", etcDirFile.getAbsolutePath(), line));
                return Optional.empty();
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", etcDirFile.getAbsolutePath()));
            return Optional.empty();
        }
        logger.warn(String.format("Unable to discern linux distro from %s", etcDirFile.getAbsolutePath()));
        return Optional.empty();
    }
}
