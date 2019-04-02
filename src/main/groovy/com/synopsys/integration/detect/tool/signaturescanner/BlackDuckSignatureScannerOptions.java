/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.SnippetMatching;

public class BlackDuckSignatureScannerOptions {
    private final String[] signatureScannerPaths;
    private final String[] exclusionPatterns;
    private final String[] exclusionNamePatterns;

    private final Integer scanMemory;
    private final Integer parallelProcessors;
    private final Boolean cleanupOutput;
    private final Boolean dryRun;
    private final Boolean snippetMatchingFlag;
    private final SnippetMatching snippetMatching;
    private final Boolean uploadSource;
    private final String codeLocationPrefix;
    private final String codeLocationSuffix;
    private final String additionalArguments;
    private final Integer maxDepth;

    public BlackDuckSignatureScannerOptions(final String[] signatureScannerPaths, final String[] exclusionPatterns, final String[] exclusionNamePatterns, final Integer scanMemory, final Integer parallelProcessors,
        final Boolean cleanupOutput, final Boolean dryRun, final Boolean snippetMatchingFlag, final SnippetMatching snippetMatching, final Boolean uploadSource, final String codeLocationPrefix, final String codeLocationSuffix, final String additionalArguments, final Integer maxDepth) {
        this.signatureScannerPaths = signatureScannerPaths;
        this.exclusionPatterns = exclusionPatterns;
        this.exclusionNamePatterns = exclusionNamePatterns;
        this.scanMemory = scanMemory;
        this.parallelProcessors = parallelProcessors;
        this.cleanupOutput = cleanupOutput;
        this.dryRun = dryRun;
        this.snippetMatchingFlag = snippetMatchingFlag;
        this.snippetMatching = snippetMatching;
        this.uploadSource = uploadSource;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.additionalArguments = additionalArguments;
        this.maxDepth = maxDepth;
    }

    public String[] getSignatureScannerPaths() {
        return signatureScannerPaths;
    }

    public String[] getExclusionPatterns() {
        return exclusionPatterns;
    }

    public String[] getExclusionNamePatterns() {
        return exclusionNamePatterns;
    }

    public Integer getScanMemory() {
        return scanMemory;
    }

    /**
     * @deprecated Please use the correctly spelled getParallelProcessors().
     */
    @Deprecated
    public Integer getParrallelProcessors() {
        return parallelProcessors;
    }

    public Integer getParallelProcessors() {
        return parallelProcessors;
    }

    public Boolean getCleanupOutput() {
        return cleanupOutput;
    }

    public Boolean getDryRun() {
        return dryRun;
    }

    /**
     * @deprecated This will be replaced by the enum based property in a future release.
     */
    @Deprecated
    public Boolean getSnippetMatching() {
        return snippetMatchingFlag;
    }

    public SnippetMatching getSnippetMatchingEnum() {
        if (null == snippetMatching && snippetMatchingFlag) {
            return SnippetMatching.SNIPPET_MATCHING;
        } else {
            return snippetMatching;
        }
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

}
