/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import org.antlr.v4.runtime.misc.Nullable;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;
import com.synopsys.integration.detect.configuration.enums.IndividualFileMatchMode;

public class BlackDuckSignatureScannerOptions {
    private final List<Path> signatureScannerPaths;
    private final List<String> exclusionPatterns;
    private final List<String> exclusionNamePatterns;

    private final Path offlineLocalScannerInstallPath;
    private final Path onlineLocalScannerInstallPath;
    private final String userProvidedScannerInstallUrl;

    private final Integer scanMemory;
    private final Integer parallelProcessors;
    private final Boolean dryRun;

    @Nullable //Just to note that if you do not want snippet matching, this should be null.
    private final SnippetMatching snippetMatching;

    private final Boolean uploadSource;
    private final String codeLocationPrefix;
    private final String codeLocationSuffix;
    private final String additionalArguments;
    private final Integer maxDepth;
    private final IndividualFileMatchMode individualFileMatching;
    private final Boolean licenseSearch;

    public BlackDuckSignatureScannerOptions(
        final List<Path> signatureScannerPaths,
        final List<String> exclusionPatterns,
        final List<String> exclusionNamePatterns,
        final Path offlineLocalScannerInstallPath,
        final Path onlineLocalScannerInstallPath,
        final String userProvidedScannerInstallUrl,
        final Integer scanMemory,
        final Integer parallelProcessors,
        final Boolean dryRun,
        final SnippetMatching snippetMatching,
        final Boolean uploadSource,
        final String codeLocationPrefix,
        final String codeLocationSuffix,
        final String additionalArguments,
        final Integer maxDepth,
        final IndividualFileMatchMode individualFileMatching,
        final Boolean licenseSearch) {

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

    public SnippetMatching getSnippetMatching() {
        return snippetMatching;
    }

    public Boolean getUploadSource() {
        return uploadSource;
    }

    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }

    public String getAdditionalArguments() {
        return additionalArguments;
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

    public IndividualFileMatchMode getIndividualFileMatching() {
        return individualFileMatching;
    }

    public Boolean getLicenseSearch() {
        return licenseSearch;
    }
}
