package com.synopsys.integration.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.accuracy.DetectableEvaluationResult;
import com.synopsys.integration.detector.accuracy.DetectorEvaluation;
import com.synopsys.integration.detector.accuracy.DetectorRuleEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationUtil;

public class CodeLocationReporter {
    public void writeCodeLocationReport(
        ReportWriter writer,
        ReportWriter writer2,
        DetectorEvaluation rootEvaluation,
        Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationNameMap.keySet());
        Map<String, Integer> dependencyAggregates = counter.aggregateCountsByCreatorName(dependencyCounts);

        DetectorEvaluationUtil.allDescendentFound(rootEvaluation)
            .forEach(it -> writeBomToolEvaluationDetails(writer, it, dependencyCounts, detectCodeLocationMap, codeLocationNameMap));

        writeBomToolCounts(writer2, dependencyAggregates);

    }

    private void writeBomToolEvaluationDetails(
        ReportWriter writer,
        DetectorRuleEvaluation evaluation,
        Map<DetectCodeLocation, Integer> dependencyCounts,
        Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        List<CodeLocation> codeLocations = evaluation.getExtraction()
            .map(Extraction::getCodeLocations)
            .orElse(new ArrayList<>());

        String extractionEnvironmentId = evaluation.getExtractedDetectableEvaluation()
            .map(DetectableEvaluationResult::getExtractionEnvironment)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(it -> DetectExtractionEnvironment.class.isAssignableFrom(it.getClass()))
            .map(it -> (DetectExtractionEnvironment) it)
            .map(DetectExtractionEnvironment::getExtractionId)
            .map(ExtractionId::toUniqueString)
            .orElse("No Id");

        for (CodeLocation codeLocation : codeLocations) {
            DetectCodeLocation detectCodeLocation = detectCodeLocationMap.get(codeLocation);
            writeCodeLocationDetails(
                writer,
                detectCodeLocation,
                dependencyCounts.get(detectCodeLocation),
                codeLocationNameMap.get(detectCodeLocation),
                extractionEnvironmentId
            );
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
