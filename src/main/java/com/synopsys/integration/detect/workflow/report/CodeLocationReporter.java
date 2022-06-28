package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionId;
import com.synopsys.integration.detect.tool.detector.report.DetectorDirectoryReport;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class CodeLocationReporter {
    public void writeCodeLocationReport(
        ReportWriter writer,
        ReportWriter writer2,
        List<DetectorDirectoryReport> reports,
        Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        Map<DetectCodeLocation, String> codeLocationNameMap
    ) {
        CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationNameMap.keySet());
        Map<String, Integer> dependencyAggregates = counter.aggregateCountsByCreatorName(dependencyCounts);

        reports.forEach(report -> {
            report.getExtractedDetectors().forEach(extracted -> {
                String extractionEnvironmentId = Optional.ofNullable(extracted.getExtractedDetectable().getExtractionEnvironment())
                    .filter(it -> DetectExtractionEnvironment.class.isAssignableFrom(it.getClass()))
                    .map(it -> (DetectExtractionEnvironment) it)
                    .map(DetectExtractionEnvironment::getExtractionId)
                    .map(ExtractionId::toUniqueString)
                    .orElse("No Id");

                for (CodeLocation codeLocation : extracted.getExtractedDetectable().getExtraction().getCodeLocations()) {
                    DetectCodeLocation detectCodeLocation = detectCodeLocationMap.get(codeLocation);
                    writeCodeLocationDetails(
                        writer,
                        detectCodeLocation,
                        dependencyCounts.get(detectCodeLocation),
                        codeLocationNameMap.get(detectCodeLocation),
                        extractionEnvironmentId
                    );
                }
            });
        });

        writeBomToolCounts(writer2, dependencyAggregates);

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
