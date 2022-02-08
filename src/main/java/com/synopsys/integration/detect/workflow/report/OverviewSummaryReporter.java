package com.synopsys.integration.detect.workflow.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.report.util.ObjectPrinter;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class OverviewSummaryReporter {
    public void writeReport(ReportWriter writer, DetectorEvaluationTree rootEvaluationTree) {
        writeSummaries(writer, rootEvaluationTree.asFlatList());
    }

    private void writeSummaries(ReportWriter writer, List<DetectorEvaluationTree> detectorEvaluationTrees) {
        writer.writeSeparator();
        for (DetectorEvaluationTree detectorEvaluationTree : detectorEvaluationTrees) {
            for (DetectorEvaluation detectorEvaluation : detectorEvaluationTree.getOrderedEvaluations()) {
                if (detectorEvaluation.isSearchable() && detectorEvaluation.isApplicable()) {
                    writer.writeLine("DIRECTORY: " + detectorEvaluationTree.getDirectory());
                    writer.writeLine("DETECTOR: " + detectorEvaluation.getDetectorRule().getDescriptiveName());
                    writer.writeLine("\tEXTRACTABLE: " + detectorEvaluation.getExtractabilityMessage());
                    writer.writeLine("\tEXTRACTED: " + detectorEvaluation.wasExtractionSuccessful());
                    if (detectorEvaluation.getExtraction() != null && StringUtils.isNotBlank(detectorEvaluation.getExtraction().getDescription())) {
                        writer.writeLine("\tEXTRACTION: " + detectorEvaluation.getExtraction().getDescription());

                    }
                    Map<String, String> data = new HashMap<>();
                    ObjectPrinter.populateObjectPrivate(null, detectorEvaluation.getDetectable(), data);
                    data.forEach((key, value) -> writer.writeLine("\t" + key + ": " + value));
                }
            }
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

}
