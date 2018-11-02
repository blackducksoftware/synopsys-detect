/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class OverviewSummaryData {
    private final String directory;
    private final List<DetectorEvaluation> applicable;
    private final List<DetectorEvaluation> extractable;
    private final List<DetectorEvaluation> extractionSuccess;
    private final List<DetectorEvaluation> extractionFailure;

    public OverviewSummaryData(final String directory, final List<DetectorEvaluation> applicable, final List<DetectorEvaluation> extractable, final List<DetectorEvaluation> extractionSuccess,
        final List<DetectorEvaluation> extractionFailure) {
        this.directory = directory;
        this.applicable = applicable;
        this.extractable = extractable;
        this.extractionSuccess = extractionSuccess;
        this.extractionFailure = extractionFailure;
    }

    public List<DetectorEvaluation> getApplicable() {
        return applicable;
    }

    public List<DetectorEvaluation> getExtractable() {
        return extractable;
    }

    public List<DetectorEvaluation> getExtractionSuccess() {
        return extractionSuccess;
    }

    public List<DetectorEvaluation> getExtractionFailure() {
        return extractionFailure;
    }

    public String getDirectory() {
        return directory;
    }
}
