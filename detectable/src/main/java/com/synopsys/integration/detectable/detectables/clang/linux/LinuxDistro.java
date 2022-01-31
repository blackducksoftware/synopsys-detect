package com.synopsys.integration.detectable.detectables.clang.linux;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This might move into the libraries; it's also used by by hub-imageinspector-lib
public class LinuxDistro {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<String> extractLinuxDistroNameFromEtcDir() {
        File etcDir = new File("/etc");
        return extractLinuxDistroNameFromEtcDir(etcDir);
    }

    public Optional<String> extractLinuxDistroNameFromEtcDir(File etcDir) {
        for (File etcFile : etcDir.listFiles()) {
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
        return "lsb-release".equals(candidate.getName()) ||
            "os-release".equals(candidate.getName()) ||
            "redhat-release".equals(candidate.getName());
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
        String linePrefix;
        if ("lsb-release".equals(etcDirFile.getName())) {
            logger.trace("Found lsb-release");
            linePrefix = "DISTRIB_ID=";
        } else if ("os-release".equals(etcDirFile.getName())) {
            logger.trace("Found os-release");
            linePrefix = "ID=";
        } else {
            logger.warn("File {} is not a Linux distribution-identifying file", etcDirFile.getAbsolutePath());
            return Optional.empty();
        }
        try {
            List<String> lines = FileUtils.readLines(etcDirFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                line = line.trim();
                if (line.startsWith(linePrefix)) {
                    String[] parts = line.split("=");
                    String distroName = parts[1].replace("\"", "").toLowerCase();
                    if (StringUtils.isNotBlank(distroName)) {
                        logger.debug("Found target image Linux distro name '{}' in file {}", distroName, etcDirFile.getAbsolutePath());
                        return Optional.of(distroName);
                    }
                }
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", etcDirFile.getAbsolutePath()));
            return Optional.empty();
        }
        logger.warn("Did not find value for {} in {}", linePrefix, etcDirFile.getAbsolutePath());
        return Optional.empty();
    }

    private Optional<String> getLinuxDistroNameFromRedHatReleaseFile(File etcDirFile) {
        logger.trace("Found redhat-release");
        try {
            List<String> lines = FileUtils.readLines(etcDirFile, StandardCharsets.UTF_8);
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
                logger.warn("Found redhat-release file {} but don't understand the contents: '{}'", etcDirFile.getAbsolutePath(), line);
                return Optional.empty();
            }
        } catch (IOException e) {
            logger.error(String.format("Error reading %s", etcDirFile.getAbsolutePath()));
            return Optional.empty();
        }
        logger.warn("Unable to discern linux distro from {}", etcDirFile.getAbsolutePath());
        return Optional.empty();
    }
}
