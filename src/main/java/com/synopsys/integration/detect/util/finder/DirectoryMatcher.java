package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryMatcher {
    private static final String PATH_MATCHER_SYNTAX = "glob:%s";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean nameMatchesExludedDirectory(List<String> directoryExclusionPatterns, File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }
        return false;
    }

    public boolean pathMatchesExcludedDirectory(List<String> directoryExclusionPatterns, File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            // On Windows, patterns starting with a * raise InvalidPathException, so perform pattern check first
            // TODO: Coverity says we need to close the FileSystems.getDefault() resource, but according to: https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html
            //  "File systems created by the default provider cannot be closed."
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(String.format(PATH_MATCHER_SYNTAX, excludedDirectory));
            if (pathMatcher.matches(file.toPath())) {
                return true;
            }

            Path excludedDirectoryPath;
            try {
                excludedDirectoryPath = new File(excludedDirectory).toPath();
            } catch (InvalidPathException e) {
                logger.debug(String.format("%s could not be resolved to a path.", excludedDirectory));
                continue;
            }

            if (file.toPath().endsWith(excludedDirectoryPath)) {
                return true;
            }
        }
        return false;
    }
}
