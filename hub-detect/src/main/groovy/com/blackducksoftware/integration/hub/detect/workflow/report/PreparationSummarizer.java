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
import java.util.Optional;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class PreparationSummarizer extends DetectorEvaluationSummarizer {
    public List<PreparationSummaryData> summarize(final List<DetectorEvaluation> results) {
        final Map<File, List<DetectorEvaluation>> byDirectory = groupByDirectory(results);

        final List<PreparationSummaryData> data = summarizeDirectory(byDirectory);

        final List<PreparationSummaryData> sorted = data.stream()
                                                        .sorted((o1, o2) -> filesystemCompare(o1.getDirectory(), o2.getDirectory()))
                                                        .collect(Collectors.toList());

        return sorted;
    }

    private List<PreparationSummaryData> summarizeDirectory(final Map<File, List<DetectorEvaluation>> byDirectory) {
        return byDirectory.entrySet().stream()
                   .map(it -> createData(it.getKey().toString(), it.getValue()))
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .collect(Collectors.toList());
    }

    private Optional<PreparationSummaryData> createData(final String directory, final List<DetectorEvaluation> evaluations) {
        final List<DetectorEvaluation> applicable = evaluations.stream()
                                                        .filter(it -> it.isApplicable())
                                                        .collect(Collectors.toList());

        final List<DetectorEvaluation> ready = applicable.stream()
                                                   .filter(it -> it.isExtractable())
                                                   .collect(Collectors.toList());

        final List<DetectorEvaluation> failed = applicable.stream()
                                                    .filter(it -> !it.isExtractable())
                                                    .collect(Collectors.toList());

        if (ready.size() > 0 || failed.size() > 0) {
            final PreparationSummaryData data = new PreparationSummaryData(directory, ready, failed);
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

}
