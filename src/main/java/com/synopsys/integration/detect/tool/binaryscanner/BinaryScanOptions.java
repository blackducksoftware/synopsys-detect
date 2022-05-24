package com.synopsys.integration.detect.tool.binaryscanner;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detect.util.finder.DetectExcludedDirectoryFilter;

public class BinaryScanOptions {
    private final Path singleTargetFilePath;
    private final List<String> multipleTargetFileNamePatterns;
    private final Predicate<File> fileFilter;
    private final int searchDepth;
    private final boolean followSymLinks;

    public BinaryScanOptions(
        @Nullable Path singleTargetFilePath,
        List<String> multipleTargetFileNamePatterns,
        Predicate<File> fileFilter,
        int searchDepth,
        boolean followSymLinks
    ) {
        this.singleTargetFilePath = singleTargetFilePath;
        this.multipleTargetFileNamePatterns = multipleTargetFileNamePatterns;
        this.fileFilter = fileFilter;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
    }

    public List<String> getMultipleTargetFileNamePatterns() {
        return multipleTargetFileNamePatterns;
    }

    public Optional<Path> getSingleTargetFilePath() {
        return Optional.ofNullable(singleTargetFilePath);
    }

    public Predicate<File> getFileFilter() {
        return fileFilter;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }
}
