package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectExcludedDirectoryFilter implements Predicate<File> {
    private static final String PATH_MATCHER_SYNTAX = "glob:%s";
    private final List<String> directoryExclusionPatterns;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DetectExcludedDirectoryFilter(List<String> directoryExclusionPatterns) {
        this.directoryExclusionPatterns = directoryExclusionPatterns;
    }

    @Override
    public boolean test(File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(File file) {
        return nameMatches(file) || pathMatches(file);
    }

    private boolean nameMatches(File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }
        return false;
    }

    private boolean pathMatches(File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            // On Windows, patterns starting with a * raise InvalidPathException, so perform pattern check first
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
