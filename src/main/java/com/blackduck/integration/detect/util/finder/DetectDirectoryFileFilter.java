package com.blackduck.integration.detect.util.finder;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectDirectoryFileFilter implements Predicate<File>  {
    private final List<String> directoryExclusionPatterns;
    private final DirectoryMatcher directoryMatcher = new DirectoryMatcher();
    private final WildcardFileFilter wildcardFilter;

    public DetectDirectoryFileFilter(List<String> directoryExclusionPatterns, List<String> fileInclusionPatterns) {
        this.directoryExclusionPatterns = directoryExclusionPatterns;
        this.wildcardFilter = new WildcardFileFilter(fileInclusionPatterns);
    }

    @Override
    public boolean test(File file) {
        if (file.isDirectory()) {
            return directoryMatcher.nameMatchesExludedDirectory(directoryExclusionPatterns, file) || directoryMatcher.pathMatchesExcludedDirectory(directoryExclusionPatterns, file);
        }
        if (file.isFile()) {
            return wildcardFilter.accept(file);
        }
        return false;
    }
}
