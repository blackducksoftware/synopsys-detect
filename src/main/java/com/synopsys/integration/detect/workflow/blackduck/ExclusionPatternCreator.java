package com.synopsys.integration.detect.workflow.blackduck;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;

public class ExclusionPatternCreator {
    private final Logger logger = LoggerFactory.getLogger(ExclusionPatternCreator.class);

    private final FileFinder fileFinder;
    private final Predicate<File> fileFilter;
    private final File scanTarget;

    public ExclusionPatternCreator(FileFinder fileFinder, Predicate<File> fileFilter, File scanTarget) {
        this.fileFinder = fileFinder;
        this.fileFilter = fileFilter;
        this.scanTarget = scanTarget;
    }

    public Set<String> determineExclusionPatterns(boolean followSymLinks, int maxDepth, List<String> exclusionPatterns) {
        if (CollectionUtils.isEmpty(exclusionPatterns) && scanTarget.isDirectory()) {
            return Collections.emptySet();
        }
        Set<String> scanExclusionPatterns = new HashSet<>();

        // Now use patterns to resolve exclusions that we will format and pass on to the signature scanner
        try {
            String scanTargetPath = scanTarget.getCanonicalPath();
            // TODO should we only collect directories since the scanner can only exclude directories?
            List<File> matchingFiles = fileFinder.findFiles(scanTarget, fileFilter, followSymLinks, maxDepth, false); //TODO: re-add the depth hit message creator?
            for (File matchingFile : matchingFiles) {
                String matchingFilePath = matchingFile.getCanonicalPath();
                String scanExclusionPattern = createExclusionPatternFromPaths(scanTargetPath, matchingFilePath);
                scanExclusionPatterns.add(scanExclusionPattern);
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
