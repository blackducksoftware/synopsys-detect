/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.util.finder;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectExcludedDirectoryFilter implements Predicate<File> {
    private static final String PATH_MATCHER_SYNTAX = "glob:%s";
    private final Path sourcePath;
    private final List<String> excludedDirectories;
    private final List<String> excludedDirectoryPaths;
    private final WildcardFileFilter fileFilter;

    public DetectExcludedDirectoryFilter(Path sourcePath, List<String> excludedDirectories, List<String> excludedDirectoryPaths, List<String> excludedDirectoryNamePatterns) {
        this.sourcePath = sourcePath;
        this.excludedDirectories = excludedDirectories;
        this.excludedDirectoryPaths = excludedDirectoryPaths;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean test(File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(File file) {
        for (String excludedDirectory : excludedDirectories) {
            // TODO - does this check ever return true when the path loop does not?
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        // TODO - can this be the only check?
        for (String excludedDirectory : excludedDirectoryPaths) {
            Path excludedDirectoryPath = new File(excludedDirectory).toPath();
            Path relativeDirectoryPath = sourcePath.relativize(file.toPath());
            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(String.format(PATH_MATCHER_SYNTAX, excludedDirectory));

            if (relativeDirectoryPath.endsWith(excludedDirectoryPath) || pathMatcher.matches(file.toPath())) {
                return true;
            }
        }

        //TODO - would this line ever return true?
        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }

    //TODO - break each check into public methods, make isExcluded just check each &&
}
