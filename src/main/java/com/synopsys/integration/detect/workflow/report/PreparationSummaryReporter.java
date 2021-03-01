/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class PreparationSummaryReporter {

    public void write(ReportWriter writer, DetectorEvaluationTree rootEvaluationTree) {
        writeSummary(writer, rootEvaluationTree.asFlatList());
    }

    private void writeSummary(ReportWriter writer, List<DetectorEvaluationTree> detectorEvaluationTrees) {
        List<String> lines = new ArrayList<>();
        for (DetectorEvaluationTree detectorEvaluationTree : detectorEvaluationTrees) {
            List<DetectorEvaluation> applicable = DetectorEvaluationUtils.applicableChildren(detectorEvaluationTree);
            List<DetectorEvaluation> ready = applicable.stream().filter(DetectorEvaluation::isExtractable).collect(Collectors.toList());
            List<DetectorEvaluation> notExtractable = applicable.stream().filter(it -> !it.isExtractable()).collect(Collectors.toList());
            List<DetectorEvaluation> failedNoFallback = notExtractable.stream().filter(it -> !it.isFallbackExtractable()).collect(Collectors.toList());
            List<DetectorEvaluation> failedWithFallback = notExtractable.stream().filter(DetectorEvaluation::isFallbackExtractable).collect(Collectors.toList());

            List<DetectorEvaluation> skippedFallbacks = ready.stream().flatMap(it -> it.getFallbacks().stream()).collect(Collectors.toList());
            List<DetectorEvaluation> failedNotSkipped = failedNoFallback.stream()
                                                            .filter(it -> !skippedFallbacks.contains(it))
                                                            .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(ready) || CollectionUtils.isNotEmpty(notExtractable)) {
                lines.add(detectorEvaluationTree.getDirectory().toString());
                if (CollectionUtils.isNotEmpty(ready)) {
                    lines.add("\t    READY: " + ready.stream()
                                                    .map(it -> it.getDetectorRule().getDescriptiveName())
                                                    .sorted()
                                                    .collect(Collectors.joining(", ")));
                }

                lines.addAll(failedWithFallback.stream()
                                 .map(it -> "\t FALLBACK: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                 .sorted()
                                 .collect(Collectors.toList()));

                lines.addAll(failedNotSkipped.stream()
                                 .map(it -> "\t   FAILED: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                 .sorted()
                                 .collect(Collectors.toList()));

                lines.addAll(skippedFallbacks.stream()
                                 .map(it -> "\t  SKIPPED: " + it.getDetectorRule().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                                 .sorted()
                                 .collect(Collectors.toList()));
            }
        }

        if (CollectionUtils.isNotEmpty(lines)) {
            ReporterUtils.printHeader(writer, "Preparation for extraction");
            lines.forEach(writer::writeLine);
            ReporterUtils.printFooter(writer);
        }
    }

}
