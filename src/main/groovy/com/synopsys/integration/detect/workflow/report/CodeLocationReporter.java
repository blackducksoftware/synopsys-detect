/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.workflow.report;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detect.tool.detector.impl.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.report.util.DetectorEvaluationUtils;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detector.base.DetectorEvaluation;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class CodeLocationReporter {
    public void writeCodeLocationReport(final ReportWriter writer, final ReportWriter writer2, DetectorEvaluationTree rootEvaluation, Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap,
        final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        final Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationNameMap.keySet());
        final Map<String, Integer> dependencyAggregates = counter.aggregateCountsByCreatorName(dependencyCounts);

        DetectorEvaluationUtils.extractionSuccessDescendents(rootEvaluation)
            .forEach(it -> writeBomToolEvaluationDetails(writer, it, dependencyCounts, detectCodeLocationMap, codeLocationNameMap));

        writeBomToolCounts(writer2, dependencyAggregates);

    }

    private void writeBomToolEvaluationDetails(final ReportWriter writer, final DetectorEvaluation evaluation, final Map<DetectCodeLocation, Integer> dependencyCounts, Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        for (final CodeLocation codeLocation : evaluation.getExtraction().getCodeLocations()) {
            DetectExtractionEnvironment detectExtractionEnvironment = (DetectExtractionEnvironment) evaluation.getExtractionEnvironment();
            DetectCodeLocation detectCodeLocation = detectCodeLocationMap.get(codeLocation);
            writeCodeLocationDetails(writer, detectCodeLocation, dependencyCounts.get(detectCodeLocation), codeLocationNameMap.get(detectCodeLocation), detectExtractionEnvironment.getExtractionId().toUniqueString());
        }
    }

    private void writeCodeLocationDetails(final ReportWriter writer, final DetectCodeLocation codeLocation, final Integer dependencyCount, final String codeLocationName, final String extractionId) {
        writer.writeSeperator();
        writer.writeLine("Name : " + codeLocationName);
        writer.writeLine("Directory : " + codeLocation.getSourcePath());
        writer.writeLine("Extraction : " + extractionId);
        writer.writeLine("Detect Code Location Type : " + codeLocation.getCreatorName());

        final DependencyGraph graph = codeLocation.getDependencyGraph();

        writer.writeLine("Root Dependencies : " + graph.getRootDependencies().size());
        writer.writeLine("Total Dependencies : " + dependencyCount);
    }

    private void writeBomToolCounts(final ReportWriter writer, final Map<String, Integer> dependencyCounts) {
        for (final String group : dependencyCounts.keySet()) {
            final Integer count = dependencyCounts.get(group);

            writer.writeLine(group + " : " + count);
        }
    }
}
