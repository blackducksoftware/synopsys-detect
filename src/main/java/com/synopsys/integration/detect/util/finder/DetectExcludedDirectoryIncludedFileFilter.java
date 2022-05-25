package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectExcludedDirectoryIncludedFileFilter implements Predicate<File>  {
    private final List<String> directoryExclusionPatterns;
    private final DirectoryMatcher directoryMatcher = new DirectoryMatcher();
    private final WildcardFileFilter wildcardFilter;

    public DetectExcludedDirectoryIncludedFileFilter(List<String> directoryExclusionPatterns, List<String> fileInclusionPatterns) {
        this.directoryExclusionPatterns = directoryExclusionPatterns;
        this.wildcardFilter = new WildcardFileFilter(fileInclusionPatterns);
    }

    @Override
    public boolean test(File file) {
        return file.exists() && !isExcludedDirectory(file) && !isExcludedFile(file);
    }

    public boolean isExcludedDirectory(File file) {
        if (!file.isDirectory()) {
            return false;
        }
        return directoryMatcher.nameMatchesExludedDirectory(directoryExclusionPatterns, file) || directoryMatcher.pathMatchesExcludedDirectory(directoryExclusionPatterns, file);
    }

    private boolean isExcludedFile(File file) {
        if (!file.isFile()) {
            return false;
        }
        return !wildcardFilter.accept(file);
    }
}
