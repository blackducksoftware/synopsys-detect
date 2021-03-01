/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
    public void writeCodeLocationReport(final ReportWriter writer, final ReportWriter writer2, final DetectorEvaluationTree rootEvaluation, final Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        final Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationNameMap.keySet());
        final Map<String, Integer> dependencyAggregates = counter.aggregateCountsByCreatorName(dependencyCounts);

        DetectorEvaluationUtils.extractionSuccessDescendents(rootEvaluation)
            .forEach(it -> writeBomToolEvaluationDetails(writer, it, dependencyCounts, detectCodeLocationMap, codeLocationNameMap));

        writeBomToolCounts(writer2, dependencyAggregates);

    }

    private void writeBomToolEvaluationDetails(final ReportWriter writer, final DetectorEvaluation evaluation, final Map<DetectCodeLocation, Integer> dependencyCounts, final Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        final Map<DetectCodeLocation, String> codeLocationNameMap) {
        for (final CodeLocation codeLocation : evaluation.getExtraction().getCodeLocations()) {
            final DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) evaluation.getExtractionEnvironment();
            final DetectCodeLocation detectCodeLocation = detectCodeLocationMap.get(codeLocation);
            writeCodeLocationDetails(writer, detectCodeLocation, dependencyCounts.get(detectCodeLocation), codeLocationNameMap.get(detectCodeLocation), detectExtractionEnvironment.getExtractionId().toUniqueString());
        }
    }

    private void writeCodeLocationDetails(final ReportWriter writer, final DetectCodeLocation codeLocation, final Integer dependencyCount, final String codeLocationName, final String extractionId) {
        writer.writeSeparator();
        writer.writeLine("Name : " + codeLocationName);
        writer.writeLine("Directory : " + codeLocation.getSourcePath());
        writer.writeLine("Extraction : " + extractionId);
        writer.writeLine("Detect Code Location Type : " + codeLocation.getCreatorName());

        final DependencyGraph graph = codeLocation.getDependencyGraph();

        writer.writeLine("Root Dependencies : " + graph.getRootDependencies().size());
        writer.writeLine("Total Dependencies : " + dependencyCount);
    }

    private void writeBomToolCounts(final ReportWriter writer, final Map<String, Integer> dependencyCounts) {
        for (final Map.Entry<String, Integer> groupCountEntry : dependencyCounts.entrySet()) {
            final String group = groupCountEntry.getKey();
            final Integer count = groupCountEntry.getValue();

            writer.writeLine(group + " : " + count);
        }
    }
}
