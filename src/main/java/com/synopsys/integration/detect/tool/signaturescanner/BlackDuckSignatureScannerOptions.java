/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.IndividualFileMatching;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;

public class BlackDuckSignatureScannerOptions {
    private final List<Path> signatureScannerPaths;
    private final List<String> exclusionPatterns;
    private final List<String> exclusionNamePatterns;

    @Nullable
    private final Path offlineLocalScannerInstallPath;
    @Nullable
    private final Path onlineLocalScannerInstallPath;
    @Nullable
    private final String userProvidedScannerInstallUrl;

    private final Integer scanMemory;
    private final Integer parallelProcessors;
    private final Boolean dryRun;

    @Nullable //Just to note that if you do not want snippet matching, this should be null.
    private final SnippetMatching snippetMatching;

    private final boolean uploadSource;
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;
    @Nullable
    private final String additionalArguments;
    private final Integer maxDepth;
    @Nullable
    private final IndividualFileMatching individualFileMatching;
    private final Boolean licenseSearch;
    private final Boolean copyrightSearch;
    private final Boolean followSymLinks;

    public BlackDuckSignatureScannerOptions(
        List<Path> signatureScannerPaths,
        List<String> exclusionPatterns,
        List<String> exclusionNamePatterns,
        @Nullable Path offlineLocalScannerInstallPath,
        @Nullable Path onlineLocalScannerInstallPath,
        @Nullable String userProvidedScannerInstallUrl,
        Integer scanMemory,
        Integer parallelProcessors,
        Boolean dryRun,
        @Nullable SnippetMatching snippetMatching,
        @Nullable Boolean uploadSource,
        @Nullable String codeLocationPrefix,
        @Nullable String codeLocationSuffix,
        @Nullable String additionalArguments,
        Integer maxDepth,
        @Nullable IndividualFileMatching individualFileMatching,
        Boolean licenseSearch,
        Boolean copyrightSearch,
        Boolean followSymLinks) {

        this.signatureScannerPaths = signatureScannerPaths;
        this.exclusionPatterns = exclusionPatterns;
        this.exclusionNamePatterns = exclusionNamePatterns;
        this.offlineLocalScannerInstallPath = offlineLocalScannerInstallPath;
        this.onlineLocalScannerInstallPath = onlineLocalScannerInstallPath;
        this.userProvidedScannerInstallUrl = userProvidedScannerInstallUrl;
        this.scanMemory = scanMemory;
        this.parallelProcessors = parallelProcessors;
        this.dryRun = dryRun;
        this.snippetMatching = snippetMatching;
        this.uploadSource = uploadSource;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.additionalArguments = additionalArguments;
        this.maxDepth = maxDepth;
        this.individualFileMatching = individualFileMatching;
        this.licenseSearch = licenseSearch;
        this.copyrightSearch = copyrightSearch;
        this.followSymLinks = followSymLinks;
    }

    public List<Path> getSignatureScannerPaths() {
        return signatureScannerPaths;
    }

    public List<String> getExclusionPatterns() {
        return exclusionPatterns;
    }

    public List<String> getExclusionNamePatterns() {
        return exclusionNamePatterns;
    }

    public Integer getScanMemory() {
        return scanMemory;
    }

    public Integer getParallelProcessors() {
        return parallelProcessors;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    public Optional<SnippetMatching> getSnippetMatching() {
        return Optional.ofNullable(snippetMatching);
    }

    public Boolean getUploadSource() {
        return uploadSource;
    }

    public Optional<String> getCodeLocationPrefix() {
        return Optional.ofNullable(codeLocationPrefix);
    }

    public Optional<String> getCodeLocationSuffix() {
        return Optional.ofNullable(codeLocationSuffix);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public Optional<Path> getOfflineLocalScannerInstallPath() {
        return Optional.ofNullable(offlineLocalScannerInstallPath);
    }

    public Optional<Path> getOnlineLocalScannerInstallPath() {
        return Optional.ofNullable(onlineLocalScannerInstallPath);
    }

    public Optional<String> getUserProvidedScannerInstallUrl() {
        return Optional.ofNullable(userProvidedScannerInstallUrl);
    }

    public Optional<IndividualFileMatching> getIndividualFileMatching() {
        return Optional.ofNullable(individualFileMatching);
    }

    public Boolean getLicenseSearch() {
        return licenseSearch;
    }

    public Boolean getCopyrightSearch() {
        return copyrightSearch;
    }

    public Boolean followSymLinks() {
        return followSymLinks;
    }
}
