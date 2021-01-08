/**
 * detectable
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
package com.synopsys.integration.detectable.detectable.evaluation;

import java.util.Optional;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;

public class DetectableEvaluation {
    public static final String NO_MESSAGE = "Unknown";

    private final Detectable detectable;
    private final DetectableEnvironment environment;

    private DetectableResult searchable;
    private DetectableResult applicable;
    private DetectableResult extractable;

    private ExtractionEnvironment extractionEnvironment;
    private Extraction extraction;

    public DetectableEvaluation(final Detectable detectable, final DetectableEnvironment environment) {
        this.detectable = detectable;
        this.environment = environment;
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
        return isExtractable() && this.extraction != null && this.extraction.isSuccess();
    }

    public Detectable getDetectable() {
        return detectable;
    }

    public DetectableEnvironment getEnvironment() {
        return environment;
    }

    public void setSearchable(final DetectableResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getBomToolResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(final DetectableResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public String getApplicabilityMessage() {
        return getBomToolResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(final DetectableResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public String getExtractabilityMessage() {
        return getBomToolResultDescription(extractable).orElse(NO_MESSAGE);
    }

    private Optional<String> getBomToolResultDescription(final DetectableResult detectableResult) {
        String description = null;

        if (detectableResult != null) {
            description = detectableResult.toDescription();
        }

        return Optional.ofNullable(description);
    }

}
