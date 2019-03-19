package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.synopsys.integration.util.ExcludedIncludedFilter;

public class DetectDetectorFileFilter implements Predicate<File> {
    private List<String> excludedDirectories;
    private WildcardFileFilter fileFilter;

    public DetectDetectorFileFilter(List<String> excludedDirectories, List<String> excludedDirectoryNamePatterns){
        this.excludedDirectories = excludedDirectories;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean test(final File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(final File file){
        for (final String excludedDirectory : excludedDirectories) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }
}
