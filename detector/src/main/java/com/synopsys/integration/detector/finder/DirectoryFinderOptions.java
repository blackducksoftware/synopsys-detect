package com.synopsys.integration.detector.finder;

import java.io.File;
import java.util.function.Predicate;

public class DirectoryFinderOptions {
    private final Predicate<File> fileFilter;
    private final int maximumDepth;
    private final boolean followSymLinks;

    public DirectoryFinderOptions(Predicate<File> fileFilter, int maximumDepth, boolean followSymLinks) {
        this.fileFilter = fileFilter;
        this.maximumDepth = maximumDepth;
        this.followSymLinks = followSymLinks;
    }

    public Predicate<File> getFileFilter() {
        return fileFilter;
    }

    public int getMaximumDepth() {
        return maximumDepth;
    }

    public boolean followSymLinks() {
        return followSymLinks;
    }
}
