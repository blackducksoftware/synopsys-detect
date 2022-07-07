package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryMatcher {
    private static final String PATH_MATCHER_SYNTAX = "glob:%s";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean nameMatchesExcludedDirectory(List<String> directoryExclusionPatterns, File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }
        return false;
    }

    public boolean pathMatchesExcludedDirectory(List<String> directoryExclusionPatterns, File file) {
        try (FileSystem fileSystemDefaults = FileSystems.getDefault()) {
            for (String excludedDirectory : directoryExclusionPatterns) {
                // On Windows, patterns starting with a * raise InvalidPathException, so perform pattern check first
                PathMatcher pathMatcher = fileSystemDefaults.getPathMatcher(String.format(PATH_MATCHER_SYNTAX, excludedDirectory));
                if (pathMatcher.matches(file.toPath())) {
                    return true;
                }

                Path excludedDirectoryPath = createExcludedDirectoryPath(excludedDirectory);
                if (excludedDirectoryPath == null || file.toPath().endsWith(excludedDirectoryPath)) {
                    return true;
                }
            }
        } catch (IOException e) {
            logger.debug("Failure using FileSystem for Excluded Directory pattern matching.", e);
        }
        return false;
    }

    @Nullable
    private Path createExcludedDirectoryPath(String excludedDirectory) {
        try {
            return new File(excludedDirectory).toPath();
        } catch (InvalidPathException e) {
            logger.debug(String.format("%s could not be resolved to a path.", excludedDirectory));
            return null;
        }
    }
}
