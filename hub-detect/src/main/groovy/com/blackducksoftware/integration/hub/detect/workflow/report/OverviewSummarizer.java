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

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class OverviewSummarizer extends BomToolEvaluationSummarizer {

    public List<OverviewSummaryData> summarize(final List<BomToolEvaluation> evaluations) {
        final Map<File, List<BomToolEvaluation>> byDirectory = groupByDirectory(evaluations);

        final List<OverviewSummaryData> summaries = summarize(byDirectory);

        final List<OverviewSummaryData> sorted = summaries.stream()
                .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                .collect(Collectors.toList());

        return sorted;

    }

    private List<OverviewSummaryData> summarize(final Map<File, List<BomToolEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                .map(it -> createData(it.getKey().toString(), it.getValue()))
                .collect(Collectors.toList());
    }

    private OverviewSummaryData createData(final String directory, final List<BomToolEvaluation> evaluations) {
        final List<BomToolEvaluation> applicable = evaluations.stream()
                .filter(it -> it.isApplicable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractable = applicable.stream()
                .filter(it -> it.isExtractable())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractionSuccess = extractable.stream()
                .filter(it -> it.wasExtractionSuccessful())
                .collect(Collectors.toList());

        final List<BomToolEvaluation> extractionFailure = extractable.stream()
                .filter(it -> !it.wasExtractionSuccessful())
                .collect(Collectors.toList());

        return new OverviewSummaryData(directory, applicable, extractable, extractionSuccess, extractionFailure);
    }

}
