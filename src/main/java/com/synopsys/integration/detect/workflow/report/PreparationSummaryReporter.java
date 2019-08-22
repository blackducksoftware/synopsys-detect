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
package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class PreparationSummaryReporter {

    public void write(final ReportWriter writer, final DetectorEvaluationTree rootEvaluationTree) {
        writeSummary(writer, rootEvaluationTree.asFlatList());
    }

    private void writeSummary(final ReportWriter writer, final List<DetectorEvaluationTree> detectorEvaluationTrees) {
        List<String> lines = new ArrayList<>();
        for (final DetectorEvaluationTree detectorEvaluationTree : detectorEvaluationTrees) {
            List<DetectorEvaluation> applicable = DetectorEvaluationUtils.applicableChildren(detectorEvaluationTree);
            List<DetectorEvaluation> ready = applicable.stream().filter(it -> it.isExtractable()).collect(Collectors.toList());
            List<DetectorEvaluation> not_extractable = applicable.stream().filter(it -> !it.isExtractable()).collect(Collectors.toList());
            List<DetectorEvaluation> failed_no_fallback = not_extractable.stream().filter(it -> !it.isFallbackExtractable()).collect(Collectors.toList());
            List<DetectorEvaluation> failed_with_fallback = not_extractable.stream().filter(it -> it.isFallbackExtractable()).collect(Collectors.toList());

            List<DetectorEvaluation> skipped_fallbacks = ready.stream().flatMap(it -> it.getFallbacks().stream()).collect(Collectors.toList());
            List<DetectorEvaluation> failed_not_skipped = failed_no_fallback.stream()
                                                              .filter(it -> !skipped_fallbacks.contains(it))
                                                              .collect(Collectors.toList());

            if (ready.size() > 0 || not_extractable.size() > 0) {
                lines.add(detectorEvaluationTree.getDirectory().toString());
                if (ready.size() > 0) {
                    lines.add("\t    READY: " + ready.stream()
                                                    .map(it -> it.getDetectorRule().getDescriptiveName())
                                                    .sorted()
                                                    .collect(Collectors.joining(", ")));
                }
                if (failed_with_fallback.size() > 0) {
                    lines.addAll(failed_with_fallback.stream()
                                     .map(it -> "\t FALLBACK: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                     .sorted()
                                     .collect(Collectors.toList()));
                }
                if (failed_not_skipped.size() > 0) {
                    lines.addAll(failed_not_skipped.stream()
                                     .map(it -> "\t   FAILED: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                     .sorted()
                                     .collect(Collectors.toList()));
                }
                if (skipped_fallbacks.size() > 0) {
                    lines.addAll(skipped_fallbacks.stream()
                                     .map(it -> "\t  SKIPPED: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                     .sorted()
                                     .collect(Collectors.toList()));
                }
            }
        }
        if (lines.size() > 0) {
            ReporterUtils.printHeader(writer, "Preparation for extraction");
            lines.forEach(writer::writeLine);
            ReporterUtils.printFooter(writer);
        }
    }

}
