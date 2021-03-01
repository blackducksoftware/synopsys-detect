/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.binaryscanner;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BinaryScanOptions {
    private final Path singleTargetFilePath;
    private final List<String> multipleTargetFileNamePatterns;

    private final String codeLocationPrefix;
    private final String codeLocationSuffix;
    private final int searchDepth;

    public BinaryScanOptions(@Nullable Path singleTargetFilePath, List<String> multipleTargetFileNamePatterns, String codeLocationPrefix, String codeLocationSuffix, int searchDepth) {
        this.singleTargetFilePath = singleTargetFilePath;
        this.multipleTargetFileNamePatterns = multipleTargetFileNamePatterns;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.searchDepth = searchDepth;
    }

    public List<String> getMultipleTargetFileNamePatterns() {
        return multipleTargetFileNamePatterns;
    }

    public Optional<Path> getSingleTargetFilePath() {
        return Optional.ofNullable(singleTargetFilePath);
    }

    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }

    public int getSearchDepth() {
        return searchDepth;
    }
}
