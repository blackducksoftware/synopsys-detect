package com.synopsys.integration.detect.workflow.report;

import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;

// TODO(detectors): Decide if this can be removed
public class DetailedSearchSummaryReporter {
    public void print(ReportWriter writer, List<DetectorDirectoryReport> rootEvaluation) {
        //        printDirectoriesDebug(writer, DetectorEvaluationUtil.asFlatList(rootEvaluation));
    }

    //    private void printDirectoriesDebug(ReportWriter writer, List<DetectorEvaluation> trees) {
    //        for (DetectorEvaluation tree : trees) {
    //            List<String> toPrint = new ArrayList<>();
    //            toPrint.addAll(printDetails("      APPLIED: ", DetectorEvaluationUtils.applicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));
    //            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.notSearchableChildren(tree), DetectorEvaluation::getSearchabilityMessage));
    //            toPrint.addAll(printDetails("DID NOT APPLY: ", DetectorEvaluationUtils.searchableButNotApplicableChildren(tree), DetectorEvaluation::getApplicabilityMessage));
    //
    //            if (toPrint.size() > 0) {
    //                writer.writeSeparator();
    //                writer.writeLine("Detailed search results for directory");
    //                writer.writeLine(tree.getDirectory().toString());
    //                writer.writeSeparator();
    //                toPrint.stream().sorted().forEach(writer::writeLine);
    //                writer.writeSeparator();
    //            }
    //        }
    //    }
    //
    //    private List<String> printDetails(String prefix, List<DetectorEvaluation> details, Function<DetectorEvaluation, String> reason) {
    //        List<String> toPrint = new ArrayList<>();
    //        for (DetectorEvaluation detail : details) {
    //            toPrint.add(prefix + detail.getDetectorRule().getDescriptiveName() + ": " + reason.apply(detail));
    //        }
    //        return toPrint;
    //    }
}
