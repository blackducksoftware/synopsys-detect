/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

    public BlackDuckSignatureScannerOptions(
        final List<Path> signatureScannerPaths,
        final List<String> exclusionPatterns,
        final List<String> exclusionNamePatterns,
        @Nullable final Path offlineLocalScannerInstallPath,
        @Nullable final Path onlineLocalScannerInstallPath,
        @Nullable final String userProvidedScannerInstallUrl,
        final Integer scanMemory,
        final Integer parallelProcessors,
        final Boolean dryRun,
        @Nullable final SnippetMatching snippetMatching,
        @Nullable final Boolean uploadSource,
        @Nullable final String codeLocationPrefix,
        @Nullable final String codeLocationSuffix,
        @Nullable final String additionalArguments,
        final Integer maxDepth,
        @Nullable final IndividualFileMatching individualFileMatching,
        final Boolean licenseSearch,
        final Boolean copyrightSearch) {

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
}
