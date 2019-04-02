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
package com.synopsys.integration.detect.workflow.search.result;

import java.util.Optional;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.workflow.extraction.Extraction;

public class DetectorEvaluation {
    public static final String NO_MESSAGE = "Unknown";

    private final Detector detector;
    private final DetectorEnvironment environment;

    private DetectorResult searchable;
    private DetectorResult applicable;
    private DetectorResult extractable;

    private ExtractionId extractionId;
    private Extraction extraction;

    public DetectorEvaluation(final Detector detector, final DetectorEnvironment environment) {
        this.detector = detector;
        this.environment = environment;
    }

    public void setExtraction(final Extraction extraction) {
        this.extraction = extraction;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtractionId(final ExtractionId extractionId) {
        this.extractionId = extractionId;
    }

    public ExtractionId getExtractionId() {
        return extractionId;
    }

    public boolean wasExtractionSuccessful() {
        return isExtractable() && this.extraction != null && this.extraction.result == Extraction.ExtractionResultType.SUCCESS;
    }

    public Detector getDetector() {
        return detector;
    }

    public DetectorEnvironment getEnvironment() {
        return environment;
    }

    public void setSearchable(final DetectorResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getBomToolResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(final DetectorResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public String getApplicabilityMessage() {
        return getBomToolResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(final DetectorResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public String getExtractabilityMessage() {
        return getBomToolResultDescription(extractable).orElse(NO_MESSAGE);
    }

    private Optional<String> getBomToolResultDescription(final DetectorResult detectorResult) {
        String description = null;

        if (detectorResult != null) {
            description = detectorResult.toDescription();
        }

        return Optional.ofNullable(description);
    }

}
