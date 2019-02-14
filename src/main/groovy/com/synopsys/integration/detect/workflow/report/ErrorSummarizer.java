/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.report;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class ErrorSummarizer extends DetectorEvaluationSummarizer {
    public List<ErrorSummaryData> summarize(final List<DetectorEvaluation> results) {
        final Map<File, List<DetectorEvaluation>> byDirectory = groupByDirectory(results);

        final List<ErrorSummaryData> data = createSummaries(byDirectory);

        final List<ErrorSummaryData> sorted = data.stream()
                                                  .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                                                  .collect(Collectors.toList());

        return sorted;
    }

    private List<ErrorSummaryData> createSummaries(final Map<File, List<DetectorEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                   .map(it -> createData(it.getKey().toString(), it.getValue()))
                   .collect(Collectors.toList());

    }

    private ErrorSummaryData createData(final String directory, final List<DetectorEvaluation> evaluations) {

        final List<ErrorSummaryBomToolError> notExtractable = evaluations.stream()
                                                                  .filter(it -> it.isApplicable() && !it.isExtractable())
                                                                  .map(it -> new ErrorSummaryBomToolError(it.getDetector().getDescriptiveName(), it.getExtractabilityMessage()))
                                                                  .collect(Collectors.toList());

        final List<DetectorEvaluation> extractions = evaluations.stream()
                                                         .filter(it -> it.getExtraction() != null)
                                                         .collect(Collectors.toList());

        final List<ErrorSummaryBomToolError> failure = extractions.stream()
                                                           .filter(it -> it.getExtraction().result == Extraction.ExtractionResultType.FAILURE)
                                                           .map(it -> new ErrorSummaryBomToolError(it.getDetector().getDescriptiveName(), it.getExtraction().description))
                                                           .collect(Collectors.toList());

        final List<ErrorSummaryBomToolError> exception = extractions.stream()
                                                             .filter(it -> it.getExtraction().result == Extraction.ExtractionResultType.EXCEPTION)
                                                             .map(it -> new ErrorSummaryBomToolError(it.getDetector().getDescriptiveName(), getExceptionMessage(it.getExtraction())))
                                                             .collect(Collectors.toList());

        return new ErrorSummaryData(directory, notExtractable, failure, exception);
    }

    private String getExceptionMessage(Extraction extraction) {
        if (extraction.error != null) {
            return extraction.error.toString();
        } else {
            return extraction.description;
        }
    }

}
