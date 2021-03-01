/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DiscoverySummaryReporter {

    public void writeSummary(final ReportWriter writer, final DetectorEvaluationTree rootEvaluation) {
        ReporterUtils.printHeader(writer, "Discovery results:");
        boolean printedAny = false;
        for (final DetectorEvaluationTree it : rootEvaluation.asFlatList()) {
            final List<DetectorEvaluation> success = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasDiscoverySuccessful);
            final List<DetectorEvaluation> exception = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasDiscoveryException);
            final List<DetectorEvaluation> failed = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasDiscoveryFailure);

            int count = success.size() + failed.size() + exception.size();
            if (count > 0) {
                writer.writeLine(it.getDirectory().toString());
                writer.writeLine("\tProject Information Discoveries: " + count);
                writeEvaluationsIfNotEmpty(writer, "\tSuccess: ", success);
                writeEvaluationsIfNotEmpty(writer, "\tFailure: ", failed);
                writeEvaluationsIfNotEmpty(writer, "\tException: ", exception);
                printedAny = true;
            }
        }
        if (!printedAny) {
            writer.writeLine("There were no extractions to be summarized - no code locations were generated or no detectors were evaluated.");
        }
        ReporterUtils.printFooter(writer);
    }

    private void writeEvaluationsIfNotEmpty(final ReportWriter writer, final String prefix, final List<DetectorEvaluation> evaluations) {
        if (evaluations.size() > 0) {
            writer.writeLine(prefix + evaluations.stream().map(evaluation -> evaluation.getDetectorRule().getDescriptiveName()).collect(Collectors.joining(", ")));
        }
    }

}
