/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.file;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.common.util.finder.WildcardFileFinder;

public class FilteredFileFinder extends WildcardFileFinder {
    private final List<String> excludedFileNames;

    public FilteredFileFinder(List<String> excludedFileNames) {
        this.excludedFileNames = excludedFileNames;
    }

    @NotNull
    @Override
    public List<File> findFiles(File directoryToSearch, List<String> filenamePatterns, boolean followSymLinks, int depth, boolean findInsideMatchingDirectories) {
        return super.findFiles(directoryToSearch, filenamePatterns, followSymLinks, depth, findInsideMatchingDirectories).stream()
                   .filter(file -> !excludedFileNames.contains(file.getName()))
                   .collect(Collectors.toList());
    }
}