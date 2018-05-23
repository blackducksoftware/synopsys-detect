package com.blackducksoftware.integration.hub.detect.hub;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class ExclusionPatternDetector {
    private final Logger logger = LoggerFactory.getLogger(ExclusionPatternDetector.class);

    private final DetectFileFinder detectFileFinder;
    private final File scanTarget;

    public ExclusionPatternDetector(DetectFileFinder detectFileFinder, File scanTarget) {
        this.detectFileFinder = detectFileFinder;
        this.scanTarget = scanTarget;
    }

    public Set<String> determineExclusionPatterns(String... exclusionFileNameGlobalPatterns) {
        if (null == exclusionFileNameGlobalPatterns || exclusionFileNameGlobalPatterns.length < 1 && scanTarget.isDirectory()) {
            return Collections.emptySet();
        }
        Set<String> scanExclusionPatterns = new HashSet<>();
        try {
            String scanTargetPath = scanTarget.getCanonicalPath();
            for (String exclusionFileNamePattern : exclusionFileNameGlobalPatterns) {
                List<File> matchingFiles = detectFileFinder.findAllFilesToMaxDepth(scanTarget, exclusionFileNamePattern, false);
                for (File matchingFile : matchingFiles) {
                    String matchingFilePath = matchingFile.getCanonicalPath();
                    String scanExclusionPattern = createExclusionPatternFromPaths(scanTargetPath, matchingFilePath);
                    scanExclusionPatterns.add(scanExclusionPattern);
                }
            }
        } catch (IOException e) {
            logger.warn("Problem encountered finding the exclusion patterns for the scanner. " + e.getMessage());
            logger.debug(e.getMessage(), e);
        }
        return scanExclusionPatterns;
    }

    private String createExclusionPatternFromPaths(String rootPath, String targetPath) {
        String scanExclusionPattern = targetPath.replace(rootPath, "/");
        if (scanExclusionPattern.contains("\\\\")) {
            scanExclusionPattern = scanExclusionPattern.replace("\\\\", "/");
        }
        if (scanExclusionPattern.contains("\\")) {
            scanExclusionPattern = scanExclusionPattern.replace("\\", "/");
        }
        if (scanExclusionPattern.contains("//")) {
            scanExclusionPattern = scanExclusionPattern.replace("//", "/");
        }
        if (!scanExclusionPattern.endsWith("/")) {
            scanExclusionPattern = scanExclusionPattern + "/";
        }
        return scanExclusionPattern;
    }
}
