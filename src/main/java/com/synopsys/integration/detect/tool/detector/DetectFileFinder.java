package com.synopsys.integration.detect.tool.detector;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;

public class DetectFileFinder extends SimpleFileFinder {
    private final List<String> excludedFileNames;

    public DetectFileFinder(final String[] excludedFileNames) {
        this(Arrays.asList(excludedFileNames));
    }

    public DetectFileFinder(final List<String> excludedFileNames) {
        this.excludedFileNames = excludedFileNames;
    }

    @Override
    public List<File> findFiles(final File directoryToSearch, final List<String> filenamePatterns, final int depth) {
        return super.findFiles(directoryToSearch, filenamePatterns, depth).stream()
                   .filter(file -> !excludedFileNames.contains(file.getName()))
                   .collect(Collectors.toList());
    }
}
