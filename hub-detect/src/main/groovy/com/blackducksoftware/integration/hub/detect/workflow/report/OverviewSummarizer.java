/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class OverviewSummarizer extends DetectorEvaluationSummarizer {
    public List<OverviewSummaryData> summarize(final List<DetectorEvaluation> evaluations) {
        final Map<File, List<DetectorEvaluation>> byDirectory = groupByDirectory(evaluations);

        final List<OverviewSummaryData> summaries = summarize(byDirectory);

        final List<OverviewSummaryData> sorted = summaries.stream()
                                                     .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                                                     .collect(Collectors.toList());

        return sorted;

    }

    private List<OverviewSummaryData> summarize(final Map<File, List<DetectorEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                   .flatMap(it -> createData(it.getKey().toString(), it.getValue()))
                   .collect(Collectors.toList());
    }

    private Stream<OverviewSummaryData> createData(final String directory, final List<DetectorEvaluation> evaluations) {
        List<OverviewSummaryData> overviewSummaryDatas = new ArrayList<>();
        for (DetectorEvaluation evaluation : evaluations) {
            if (evaluation.isApplicable()) {
                String name = evaluation.getDetector().getName();
                boolean wasExtractable = evaluation.isExtractable();
                String error = "";
                if (!wasExtractable) {
                    error = evaluation.getExtractabilityMessage();
                }
                boolean wasExtracted = evaluation.getExtraction() != null && evaluation.getExtraction().result == Extraction.ExtractionResultType.SUCCESS;
                if (evaluation.getExtraction() != null && StringUtils.isNotBlank(evaluation.getExtraction().description)) {
                    error = evaluation.getExtraction().description;
                }
                Map<String, String> associatedData = new HashMap<>();
                ObjectPrinter.populateObjectPrivate(null, evaluation.getDetector(), associatedData);
                OverviewSummaryData overviewSummaryData = new OverviewSummaryData(directory, name, wasExtractable, wasExtracted, associatedData, error);
                overviewSummaryDatas.add(overviewSummaryData);
            }

        }

        return overviewSummaryDatas.stream();
    }

}
