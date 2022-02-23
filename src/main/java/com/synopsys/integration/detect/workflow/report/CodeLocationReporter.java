package com.synopsys.integration.detect.workflow.report;

import java.util.Map;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class CodeLocationReporter {
    public void writeCodeLocationReport(
        ReportWriter writer,
        ReportWriter writer2,
        DetectorEvaluationTree rootEvaluation,
        Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationNameMap.keySet());
        Map<String, Integer> dependencyAggregates = counter.aggregateCountsByCreatorName(dependencyCounts);

        DetectorEvaluationUtils.extractionSuccessDescendents(rootEvaluation)
            .forEach(it -> writeBomToolEvaluationDetails(writer, it, dependencyCounts, detectCodeLocationMap, codeLocationNameMap));

        writeBomToolCounts(writer2, dependencyAggregates);

    }

    private void writeBomToolEvaluationDetails(
        ReportWriter writer,
        DetectorEvaluation evaluation,
        Map<DetectCodeLocation, Integer> dependencyCounts,
        Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        for (CodeLocation codeLocation : evaluation.getExtraction().getCodeLocations()) {
            DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) evaluation.getExtractionEnvironment();
            DetectCodeLocation detectCodeLocation = detectCodeLocationMap.get(codeLocation);
            writeCodeLocationDetails(writer, detectCodeLocation, dependencyCounts.get(detectCodeLocation), codeLocationNameMap.get(detectCodeLocation), detectExtractionEnvironment.getExtractionId().toUniqueString());
        }
    }

    private void writeCodeLocationDetails(ReportWriter writer, DetectCodeLocation codeLocation, Integer dependencyCount, String codeLocationName, String extractionId) {
        writer.writeSeparator();
        writer.writeLine("Name : " + codeLocationName);
        writer.writeLine("Directory : " + codeLocation.getSourcePath());
        writer.writeLine("Extraction : " + extractionId);
        writer.writeLine("Detect Code Location Type : " + codeLocation.getCreatorName());

        DependencyGraph graph = codeLocation.getDependencyGraph();

        writer.writeLine("Root Dependencies : " + graph.getRootDependencies().size());
        writer.writeLine("Total Dependencies : " + dependencyCount);
    }

    private void writeBomToolCounts(ReportWriter writer, Map<String, Integer> dependencyCounts) {
        for (Map.Entry<String, Integer> groupCountEntry : dependencyCounts.entrySet()) {
            String group = groupCountEntry.getKey();
            Integer count = groupCountEntry.getValue();

            writer.writeLine(group + " : " + count);
        }
    }
}
