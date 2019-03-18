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

import java.util.ArrayList;
import java.util.List;
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
            List<DetectorEvaluation> failed = applicable.stream().filter(it -> !it.isExtractable()).collect(Collectors.toList());

            if (ready.size() > 0 || failed.size() > 0) {
                lines.add(detectorEvaluationTree.getDirectory().toString());
                if (ready.size() > 0) {
                    lines.add("\t READY: " + ready.stream()
                                                        .map(it -> it.getDetectorRule().getDescriptiveName())
                                                        .sorted()
                                                        .collect(Collectors.joining(", ")));
                }
                if (failed.size() > 0) {
                    lines.addAll(failed.stream()
                        .map(it -> "\tFAILED: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
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
