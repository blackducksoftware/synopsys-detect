package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

public class DetectExcludedDirectoryFilter implements Predicate<File> {
    private final List<String> directoryExclusionPatterns;
    private final DirectoryMatcher directoryMatcher = new DirectoryMatcher();

    public DetectExcludedDirectoryFilter(List<String> directoryExclusionPatterns) {
        this.directoryExclusionPatterns = directoryExclusionPatterns;
    }

    @Override
    public boolean test(File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(File file) {
        return directoryMatcher.nameMatchesExludedDirectory(directoryExclusionPatterns, file) || directoryMatcher.pathMatchesExcludedDirectory(directoryExclusionPatterns, file);
    }
}
