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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class DetailedSearchSummarizer extends DetectorEvaluationSummarizer {
    public List<DetailedSearchSummaryData> summarize(final List<DetectorEvaluation> detectorEvaluations) {
        final Map<File, List<DetectorEvaluation>> byDirectory = groupByDirectory(detectorEvaluations);
        final List<DetailedSearchSummaryData> data = createSummaries(byDirectory);
        final List<DetailedSearchSummaryData> sorted = data.stream()
                                                           .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                                                           .collect(Collectors.toList());
        return sorted;
    }

    private List<DetailedSearchSummaryData> createSummaries(final Map<File, List<DetectorEvaluation>> byDirectory) {
        final List<DetailedSearchSummaryData> datas = new ArrayList<>();
        for (final Entry<File, List<DetectorEvaluation>> entry : byDirectory.entrySet()) {
            final DetailedSearchSummaryData data = createData(entry.getKey().toString(), entry.getValue());
            datas.add(data);
        }
        return datas;
    }

    private DetailedSearchSummaryData createData(final String directory, final List<DetectorEvaluation> evaluations) {
        final List<DetailedSearchSummaryBomToolData> applicable = new ArrayList<>();
        final List<DetailedSearchSummaryBomToolData> notApplicable = new ArrayList<>();
        final List<DetailedSearchSummaryBomToolData> notSearchable = new ArrayList<>();

        for (final DetectorEvaluation evaluation : evaluations) {
            if (evaluation.isApplicable()) {
                final String reason = "Search: " + evaluation.getSearchabilityMessage() + " Applicable: " + evaluation.getApplicabilityMessage();
                applicable.add(new DetailedSearchSummaryBomToolData(evaluation.getDetector(), reason));
            } else if (evaluation.isSearchable()) {
                final String reason = evaluation.getApplicabilityMessage();
                notApplicable.add(new DetailedSearchSummaryBomToolData(evaluation.getDetector(), reason));
            } else {
                final String reason = evaluation.getSearchabilityMessage();
                notSearchable.add(new DetailedSearchSummaryBomToolData(evaluation.getDetector(), reason));
            }
        }

        return new DetailedSearchSummaryData(directory, applicable, notApplicable, notSearchable);
    }
}
