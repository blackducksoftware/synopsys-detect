/*
 * detector
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
package com.synopsys.integration.detector.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.evaluation.SearchEnvironment;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;

public class DetectorEvaluation {
    private static final String NO_MESSAGE = "Unknown";
    private static final String PASSED_RESULT = "PassedDetectorResult";

    private final DetectorRule detectorRule;
    private Detectable detectable;
    private DetectableEnvironment detectableEnvironment;
    private SearchEnvironment searchEnvironment;

    private DetectorResult searchable;
    private DetectorResult applicable;
    private DetectorResult extractable;

    private ExtractionEnvironment extractionEnvironment;
    private Extraction extraction;
    private Discovery discovery;

    private DetectorEvaluation fallbackTo;
    private DetectorEvaluation fallbackFrom;

    // The detector evaluation is built over time. The only thing you need at the start is the rule this evaluation represents.
    public DetectorEvaluation(DetectorRule detectorRule) {
        this.detectorRule = detectorRule;
    }

    public DetectorRule getDetectorRule() {
        return this.detectorRule;
    }

    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtractionEnvironment(ExtractionEnvironment extractionEnvironment) {
        this.extractionEnvironment = extractionEnvironment;
    }

    public ExtractionEnvironment getExtractionEnvironment() {
        return extractionEnvironment;
    }

    public boolean wasExtractionSuccessful() {
        return isExtractable() && this.extraction != null && this.extraction.getResult() == Extraction.ExtractionResultType.SUCCESS;
    }

    public boolean wasExtractionFailure() {
        return isExtractable() && this.extraction != null && this.extraction.getResult() == Extraction.ExtractionResultType.FAILURE;
    }

    public boolean wasExtractionException() {
        return isExtractable() && this.extraction != null && this.extraction.getResult() == Extraction.ExtractionResultType.EXCEPTION;
    }

    public boolean wasDiscoverySuccessful() {
        return isExtractable() && this.discovery != null && this.discovery.getResult() == Discovery.DiscoveryResultType.SUCCESS;
    }

    public boolean wasDiscoveryFailure() {
        return isExtractable() && this.discovery != null && this.discovery.getResult() == Discovery.DiscoveryResultType.FAILURE;
    }

    public boolean wasDiscoveryException() {
        return isExtractable() && this.discovery != null && this.discovery.getResult() == Discovery.DiscoveryResultType.EXCEPTION;
    }

    public List<Explanation> getAllExplanations() {
        List<Explanation> explanations = new ArrayList<>();
        if (applicable != null) {
            explanations.addAll(applicable.getExplanations());
        }
        if (extractable != null) {
            explanations.addAll(extractable.getExplanations());
        }
        return explanations;
    }

    public void setSearchable(DetectorResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getDetectorResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(DetectorResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public void setDiscovery(Discovery discovery) {
        this.discovery = discovery;
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    public String getApplicabilityMessage() {
        return getDetectorResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(DetectorResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public boolean isFallbackExtractable() {
        return fallbackTo != null && (fallbackTo.isExtractable() || fallbackTo.isFallbackExtractable());
    }

    public boolean isPreviousExtractable() {
        return fallbackFrom != null && (fallbackFrom.isExtractable() || fallbackFrom.isPreviousExtractable());
    }

    public String getExtractabilityMessage() {
        return getDetectorResultDescription(extractable).orElse(NO_MESSAGE);
    }

    public DetectorStatusType getStatus() {
        if (getExtraction() != null && getExtraction().getResult().equals(Extraction.ExtractionResultType.SUCCESS)) {
            return DetectorStatusType.SUCCESS;
        } else if (fallbackFrom != null && fallbackFrom.isExtractable()) {
            return DetectorStatusType.DEFERRED;
        }
        return DetectorStatusType.FAILURE;
    }

    @Nullable
    public DetectorStatusCode getStatusCode() {
        Class resultClass = null;
        if (!isSearchable()) {
            resultClass = searchable.getResultClass();
        } else if (!isApplicable()) {
            resultClass = applicable.getResultClass();
        } else if (!isExtractable()) {
            resultClass = extractable.getResultClass();
        }
        if (resultClass != null) {
            return DetectorResultStatusCodeLookup.standardLookup.getStatusCode(resultClass);
        } else if (!extraction.isSuccess()) {
            if (extraction.getError() instanceof ExecutableFailedException) {
                return DetectorStatusCode.EXECUTABLE_FAILED;
            } else {
                return DetectorStatusCode.EXTRACTION_FAILED;
            }
        } else {
            return DetectorStatusCode.PASSED;
        }
    }

    @NotNull
    public String getStatusReason() {
        if (!isSearchable()) {
            return searchable.getDescription();
        }
        if (!isApplicable()) {
            return applicable.getDescription();
        }
        if (!isExtractable()) {
            return extractable.getDescription();
        }
        if (extraction.getResult() != Extraction.ExtractionResultType.SUCCESS) {
            if (extraction.getError() instanceof ExecutableFailedException) {
                ExecutableFailedException failedException = (ExecutableFailedException) extraction.getError();
                if (failedException.hasReturnCode()) {
                    return "Failed to execute command, returned non-zero: " + failedException.getExecutableDescription();
                } else if (failedException.getExecutableException() != null) {
                    return "Failed to execute command, " + failedException.getExecutableException().getMessage() + " : " + failedException.getExecutableDescription();
                } else {
                    return "Failed to execute command, unknown reason: " + failedException.getExecutableDescription();
                }
            } else {
                return "See logs for further explanation";
            }
        }
        return "Passed";
    }

    public Optional<DetectorEvaluation> getSuccessfullFallback() {
        if (fallbackTo != null) {
            if (fallbackTo.isExtractable()) {
                return Optional.of(fallbackTo);
            } else {
                return fallbackTo.getSuccessfullFallback();
            }
        }
        return Optional.empty();
    }

    private Optional<String> getDetectorResultDescription(DetectorResult detectorResult) {
        String description = null;

        if (detectorResult != null) {
            description = detectorResult.getDescription();
        }

        return Optional.ofNullable(description);
    }

    public void setSearchEnvironment(SearchEnvironment searchEnvironment) {
        this.searchEnvironment = searchEnvironment;
    }

    public SearchEnvironment getSearchEnvironment() {
        return this.searchEnvironment;
    }

    public DetectableEnvironment getDetectableEnvironment() {
        return detectableEnvironment;
    }

    public void setDetectableEnvironment(DetectableEnvironment detectableEnvironment) {
        this.detectableEnvironment = detectableEnvironment;
    }

    public Detectable getDetectable() {
        return detectable;
    }

    public void setDetectable(Detectable detectable) {
        this.detectable = detectable;
    }

    public DetectorEvaluation getFallbackTo() {
        return fallbackTo;
    }

    public List<DetectorEvaluation> getFallbacks() {
        if (fallbackTo != null) {
            List<DetectorEvaluation> fallbacks = new ArrayList<>();
            fallbacks.add(fallbackTo);
            fallbacks.addAll(fallbackTo.getFallbacks());
            return fallbacks;
        }
        return new ArrayList<>();
    }

    public void setFallbackTo(DetectorEvaluation fallbackTo) {
        this.fallbackTo = fallbackTo;
    }

    public DetectorEvaluation getFallbackFrom() {
        return fallbackFrom;
    }

    public void setFallbackFrom(DetectorEvaluation fallbackFrom) {
        this.fallbackFrom = fallbackFrom;
    }

    public DetectorType getDetectorType() {
        return getDetectorRule().getDetectorType();
    }

    public List<File> getAllRelevantFiles() {//TODO: This technically isn't ALL relevant files, doesn't include Extraction files. Is only the search portions (applicable, extractable).
        List<File> relevant = new ArrayList<>();
        if (applicable != null) {
            relevant.addAll(applicable.getRelevantFiles());
        }
        if (extractable != null) {
            relevant.addAll(extractable.getRelevantFiles());
        }
        return relevant;
    }
}
