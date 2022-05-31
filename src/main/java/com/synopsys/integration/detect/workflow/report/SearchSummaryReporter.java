package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;

public class SearchSummaryReporter {

    public void print(ReportWriter writer, DetectorEvaluation rootEvaluation) {
        printDirectoriesInfo(writer, DetectorEvaluationUtil.asFlatList(rootEvaluation));
    }

    private void printDirectoriesInfo(ReportWriter writer, List<DetectorEvaluation> evaluations) {
        ReporterUtils.printHeader(writer, "Search results");
        boolean printedAtLeastOne = false;
        for (DetectorEvaluation evaluation : evaluations) {
            List<DetectorRuleEvaluation> applicable = evaluation.getFoundDetectorRuleEvaluations();
            if (applicable.size() > 0) {
                writer.writeLine(evaluation.getDirectory().toString());
                writer.writeLine("\tFOUND: " + applicable.stream().map(it -> it.getRule().getDetectorType().toString()).sorted().collect(Collectors.joining(", ")));
                printedAtLeastOne = true;
            }
        }
        if (!printedAtLeastOne) {
            writer.writeLine("No detectors found.");
        }
        ReporterUtils.printFooter(writer);
    }

}
