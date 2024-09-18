package com.blackduck.integration.detect.tool.binaryscanner;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

public class BinaryScanOptions {
    private final Path singleTargetFilePath;
    private final Predicate<File> fileFilter;
    private final int searchDepth;
    private final boolean followSymLinks;

    public BinaryScanOptions(
        @Nullable Path singleTargetFilePath,
        @Nullable Predicate<File> fileFilter,
        int searchDepth,
        boolean followSymLinks
    ) {
        this.singleTargetFilePath = singleTargetFilePath;
        this.fileFilter = fileFilter;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
    }

    public Optional<Path> getSingleTargetFilePath() {
        return Optional.ofNullable(singleTargetFilePath);
    }


    public Optional<Predicate<File>> getFileFilter() {
        return Optional.ofNullable(fileFilter);
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }
}
