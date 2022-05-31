package com.synopsys.integration.detect.workflow.report;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;

public class OverviewSummaryReporter {
    public void writeReport(ReportWriter writer, DetectorEvaluation rootEvaluationTree) {
        writeSummaries(writer, DetectorEvaluationUtil.asFlatList(rootEvaluationTree));
    }

    private void writeSummaries(ReportWriter writer, List<DetectorEvaluation> detectorEvaluations) {
        writer.writeSeparator();
        for (DetectorEvaluation detectorEvaluation : detectorEvaluations) {
            for (DetectorRuleEvaluation foundDetectorEvaluation : detectorEvaluation.getFoundDetectorRuleEvaluations()) {
                writer.writeLine("DIRECTORY: " + detectorEvaluation.getDirectory());
                writer.writeLine("DETECTOR: " + foundDetectorEvaluation.getRule().getDetectorType());

                for (DetectableEvaluationResult detectableEvaluation : foundDetectorEvaluation.getSelectedEntryPointEvaluation().getEvaluatedDetectables()) {
                    writer.writeLine("\tDETECTABLE: " + detectableEvaluation.getDetectableDefinition().getName());
                    boolean isExtracted = foundDetectorEvaluation.getSelectedEntryPointEvaluation().getExtractedEvaluation().map(detectableEvaluation::equals).orElse(false);
                    if (isExtracted) {
                        writer.writeLine("\tEXTRACTED: " + detectableEvaluation.wasExtractionSuccessful());
                        if (detectableEvaluation.getExtraction() != null && StringUtils.isNotBlank(detectableEvaluation.getExtraction().getDescription())) {
                            writer.writeLine("\tEXTRACTION: " + detectableEvaluation.getExtraction().getDescription());
                        }
                    }
                    detectableEvaluation.getExplanations().forEach(explanation -> {
                        writer.writeLine("\t\t" + explanation.describeSelf());
                    });

                    //TODO (detector): Should we still capture the detectable instance?
                    //                    Map<String, String> data = new HashMap<>();
                    //                    ObjectPrinter.populateObjectPrivate(null, detectableEvaluation.getDetectab.get(), data);
                    //                    data.forEach((key, value) -> writer.writeLine("\t" + key + ": " + value));
                }
            }
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

}
