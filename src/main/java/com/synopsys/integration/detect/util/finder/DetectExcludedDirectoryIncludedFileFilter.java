package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectExcludedDirectoryIncludedFileFilter implements Predicate<File>  {
    private static final String PATH_MATCHER_SYNTAX = "glob:%s";
    private final List<String> directoryExclusionPatterns;
    private final WildcardFileFilter wildcardFilter;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO write a test for this
    public DetectExcludedDirectoryIncludedFileFilter(List<String> directoryExclusionPatterns, List<String> fileInclusionPatterns) {
        this.directoryExclusionPatterns = directoryExclusionPatterns;
        this.wildcardFilter = new WildcardFileFilter(fileInclusionPatterns);
    }

    @Override
    public boolean test(File file) {
        return !isExcludedDirectory(file) && !isExcludedFile(file);
    }

    public boolean isExcludedDirectory(File file) {
        if (!file.isDirectory()) {
            return false;
        }
        return nameMatchesExludedDirectory(file) || pathMatchesExcludedDirectory(file);
    }

    private boolean nameMatchesExludedDirectory(File file) {
        for (String excludedDirectory : directoryExclusionPatterns) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }
        return false;
    }

    private boolean pathMatchesExcludedDirectory(File file) {
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

    private boolean isExcludedFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return !wildcardFilter.accept(file);
    }
}
