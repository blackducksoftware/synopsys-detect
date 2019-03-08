/**
 * detector
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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

import java.util.Optional;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detector.evaluation.SearchEnvironment;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectorRule;

public class  DetectorEvaluation {
    public static final String NO_MESSAGE = "Unknown";

    private final DetectorRule detectorRule;
    private Detectable detectable;
    private DetectableEnvironment detectableEnvironment;
    private SearchEnvironment searchEnvironment;

    private DetectorResult searchable;
    private DetectorResult applicable;
    private DetectorResult extractable;

    private ExtractionEnvironment extractionEnvironment;
    private Extraction extraction;

    // The detector evaluation is built over time. The only thing you need at the start is the rule this evaluation represents.
    public DetectorEvaluation(DetectorRule detectorRule) {
        this.detectorRule = detectorRule;
    }

    public DetectorRule getDetectorRule() {
        return this.detectorRule;
    }

    public void setExtraction(final Extraction extraction) {
        this.extraction = extraction;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtractionEnvironment(final ExtractionEnvironment extractionEnvironment) {
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

    public void setSearchable(final DetectorResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getDetectorResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(final DetectorResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public String getApplicabilityMessage() {
        return getDetectorResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(final DetectorResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public String getExtractabilityMessage() {
        return getDetectorResultDescription(extractable).orElse(NO_MESSAGE);
    }

    private Optional<String> getDetectorResultDescription(final DetectorResult detectorResult) {
        String description = null;

        if (detectorResult != null) {
            description = detectorResult.toDescription();
        }

        return Optional.ofNullable(description);
    }

    public void setSearchEnvironment(final SearchEnvironment searchEnvironment) {
        this.searchEnvironment = searchEnvironment;
    }

    public SearchEnvironment getSearchEnvironment() {
        return this.searchEnvironment;
    }

    public DetectableEnvironment getDetectableEnvironment() {
        return detectableEnvironment;
    }

    public void setDetectableEnvironment(final DetectableEnvironment detectableEnvironment) {
        this.detectableEnvironment = detectableEnvironment;
    }

    public Detectable getDetectable() {
        return detectable;
    }

    public void setDetectable(final Detectable detectable) {
        this.detectable = detectable;
    }
}
