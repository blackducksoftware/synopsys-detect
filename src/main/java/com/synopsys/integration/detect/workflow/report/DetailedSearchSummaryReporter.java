package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DetailedSearchSummaryReporter {
    public void print(ReportWriter writer, DetectorEvaluationTree rootEvaluation) {
        printDirectoriesDebug(writer, rootEvaluation.asFlatList());
    }

    private void printDirectoriesDebug(ReportWriter writer, List<DetectorEvaluationTree> trees) {
        for (DetectorEvaluationTree tree : trees) {
            List<String> toPrint = new ArrayList<>();
            toPrint.addAll(printDetails("      APPLIED: ", DetectorEvaluationUtils.applicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));
            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.notSearchableChildren(tree), DetectorEvaluation::getSearchabilityMessage));
            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.searchableButNotApplicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));

            if (toPrint.size() > 0) {
                writer.writeSeparator();
                writer.writeLine("Detailed search results for directory");
                writer.writeLine(tree.getDirectory().toString());
                writer.writeSeparator();
                toPrint.stream().sorted().forEach(writer::writeLine);
                writer.writeSeparator();
            }
        }
    }

    private List<String> printDetails(String prefix, List<DetectorEvaluation> details, Function<DetectorEvaluation, String> reason) {
        List<String> toPrint = new ArrayList<>();
        for (DetectorEvaluation detail : details) {
            toPrint.add(prefix + detail.getDetectorRule().getDescriptiveName() + ": " + reason.apply(detail));
        }
        return toPrint;
    }
}
