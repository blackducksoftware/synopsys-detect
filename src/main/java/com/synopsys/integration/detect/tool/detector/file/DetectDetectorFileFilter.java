/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.file;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

public class DetectDetectorFileFilter implements Predicate<File> {
    private final Path sourcePath;
    private final List<String> excludedDirectories;
    private final List<String> excludedDirectoryPaths;
    private final WildcardFileFilter fileFilter;

    public DetectDetectorFileFilter(final Path sourcePath, final List<String> excludedDirectories, final List<String> excludedDirectoryPaths, final List<String> excludedDirectoryNamePatterns) {
        this.sourcePath = sourcePath;
        this.excludedDirectories = excludedDirectories;
        this.excludedDirectoryPaths = excludedDirectoryPaths;
        fileFilter = new WildcardFileFilter(excludedDirectoryNamePatterns);
    }

    @Override
    public boolean test(final File file) {
        return !isExcluded(file);
    }

    public boolean isExcluded(final File file) {
        for (final String excludedDirectory : excludedDirectories) {
            if (FilenameUtils.wildcardMatchOnSystem(file.getName(), excludedDirectory)) {
                return true;
            }
        }

        for (final String excludedDirectory : excludedDirectoryPaths) {
            final Path excludedDirectoryPath = new File(excludedDirectory).toPath();
            final Path relativeDirectoryPath = sourcePath.relativize(file.toPath());
            if (relativeDirectoryPath.endsWith(excludedDirectoryPath)) {
                return true;
            }
        }

        return fileFilter.accept(file); //returns TRUE if it matches one of the file filters.
    }
}
