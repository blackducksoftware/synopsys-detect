package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.util.ReporterUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class ExtractionSummaryReporter {

    public void writeSummary(
        ReportWriter writer,
        DetectorEvaluationTree rootEvaluation,
        Map<CodeLocation, DetectCodeLocation> detectableMap,
        Map<DetectCodeLocation, String> codeLocationNameMap,
        boolean writeCodeLocationNames
    ) {
        ReporterUtils.printHeader(writer, "Extraction results:");
        boolean printedAny = false;
        for (DetectorEvaluationTree it : rootEvaluation.asFlatList()) {
            List<DetectorEvaluation> success = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasExtractionSuccessful);
            List<DetectorEvaluation> exception = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasExtractionException);
            List<DetectorEvaluation> failed = DetectorEvaluationUtils.filteredChildren(it, DetectorEvaluation::wasExtractionFailure);

            if (success.size() > 0 || exception.size() > 0 || failed.size() > 0) {
                writer.writeLine(it.getDirectory().toString());
                List<String> codeLocationNames = findCodeLocationNames(it, detectableMap, codeLocationNameMap);
                writer.writeLine("\tCode locations: " + codeLocationNames.size());
                if (writeCodeLocationNames) {
                    codeLocationNames.forEach(name -> writer.writeLine("\t\t" + name));
                }
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

    private List<String> findCodeLocationNames(
        DetectorEvaluationTree detectorEvaluationTree,
        Map<CodeLocation, DetectCodeLocation> detectableMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        List<String> codeLocationNames = new ArrayList<>();
        detectorEvaluationTree.getOrderedEvaluations().forEach(evaluation -> {
            if (evaluation.getExtraction() != null) {
                List<CodeLocation> codeLocations = evaluation.getExtraction().getCodeLocations();
                List<DetectCodeLocation> detectCodeLocations = codeLocations.stream().map(detectableMap::get).collect(Collectors.toList());
                codeLocationNames.addAll(detectCodeLocations.stream().map(codeLocationNameMap::get).collect(Collectors.toList()));
            }
        });
        return codeLocationNames;
    }

    private void writeEvaluationsIfNotEmpty(ReportWriter writer, String prefix, List<DetectorEvaluation> evaluations) {
        if (evaluations.size() > 0) {
            writer.writeLine(prefix + evaluations.stream().map(evaluation -> evaluation.getDetectorRule().getDescriptiveName()).collect(Collectors.joining(", ")));
        }
    }

}
