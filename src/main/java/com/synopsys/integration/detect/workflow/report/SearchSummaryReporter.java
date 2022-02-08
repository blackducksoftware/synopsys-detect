package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class SearchSummaryReporter {

    public void print(ReportWriter writer, DetectorEvaluationTree rootEvaluation) {
        printDirectoriesInfo(writer, rootEvaluation.asFlatList());
    }

    private void printDirectoriesInfo(ReportWriter writer, List<DetectorEvaluationTree> trees) {
        ReporterUtils.printHeader(writer, "Search results");
        boolean printedAtLeastOne = false;
        for (DetectorEvaluationTree tree : trees) {
            List<DetectorEvaluation> applicable = DetectorEvaluationUtils.applicableChildren(tree);
            if (applicable.size() > 0) {
                writer.writeLine(tree.getDirectory().toString());
                writer.writeLine("\tAPPLIES: " + applicable.stream().map(it -> it.getDetectorRule().getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
                printedAtLeastOne = true;
            }
        }
        if (!printedAtLeastOne) {
            writer.writeLine("No detectors found.");
        }
        ReporterUtils.printFooter(writer);
    }

}
