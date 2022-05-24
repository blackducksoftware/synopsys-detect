package com.synopsys.integration.detect.tool.binaryscanner;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BinaryScanOptions {
    private final Path singleTargetFilePath;
    private final List<String> multipleTargetFileNamePatterns;
    private final List<String> exclusionPatterns;
    private final int searchDepth;
    private final boolean followSymLinks;

    public BinaryScanOptions(
        @Nullable Path singleTargetFilePath,
        List<String> multipleTargetFileNamePatterns,
        List<String> exclusionPatterns,
        int searchDepth,
        boolean followSymLinks
    ) {
        this.singleTargetFilePath = singleTargetFilePath;
        this.multipleTargetFileNamePatterns = multipleTargetFileNamePatterns;
        this.exclusionPatterns = exclusionPatterns;
        this.searchDepth = searchDepth;
        this.followSymLinks = followSymLinks;
    }

    public List<String> getMultipleTargetFileNamePatterns() {
        return multipleTargetFileNamePatterns;
    }

    public Optional<Path> getSingleTargetFilePath() {
        return Optional.ofNullable(singleTargetFilePath);
    }

    public List<String> getExclusionPatterns() {
        return exclusionPatterns;
    }

    public int getSearchDepth() {
        return searchDepth;
    }

    public boolean isFollowSymLinks() {
        return followSymLinks;
    }
}
