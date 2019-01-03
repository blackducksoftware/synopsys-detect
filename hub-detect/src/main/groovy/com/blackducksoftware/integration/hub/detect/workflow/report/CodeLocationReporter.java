/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.blackducksoftware.integration.hub.detect.workflow.report.writer.ReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;
import com.synopsys.integration.bdio.graph.DependencyGraph;

public class CodeLocationReporter {
    public void writeCodeLocationReport(final ReportWriter writer, final ReportWriter writer2, final List<DetectorEvaluation> detectorEvaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final List<DetectorEvaluation> succesfullDetectorEvaluations = detectorEvaluations.stream()
                                                                           .filter(it -> it.wasExtractionSuccessful())
                                                                           .collect(Collectors.toList());

        final List<DetectCodeLocation> codeLocationsToCount = succesfullDetectorEvaluations.stream()
                                                                  .flatMap(it -> it.getExtraction().codeLocations.stream())
                                                                  .collect(Collectors.toList());

        final CodeLocationDependencyCounter counter = new CodeLocationDependencyCounter();
        final Map<DetectCodeLocation, Integer> dependencyCounts = counter.countCodeLocations(codeLocationsToCount);
        final Map<DetectCodeLocationType, Integer> dependencyAggregates = counter.aggregateCountsByGroup(dependencyCounts);

        succesfullDetectorEvaluations.forEach(it -> writeBomToolEvaluationDetails(writer, it, dependencyCounts, codeLocationNameMap));
        writeBomToolCounts(writer2, dependencyAggregates);

    }

    private void writeBomToolEvaluationDetails(final ReportWriter writer, final DetectorEvaluation evaluation, final Map<DetectCodeLocation, Integer> dependencyCounts, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        for (final DetectCodeLocation codeLocation : evaluation.getExtraction().codeLocations) {
            writeCodeLocationDetails(writer, codeLocation, dependencyCounts.get(codeLocation), codeLocationNameMap.get(codeLocation), evaluation.getExtractionId().toUniqueString());
        }
    }

    private void writeCodeLocationDetails(final ReportWriter writer, final DetectCodeLocation codeLocation, final Integer dependencyCount, final String codeLocationName, final String extractionId) {

        writer.writeSeperator();
        writer.writeLine("Name : " + codeLocationName);
        writer.writeLine("Directory : " + codeLocation.getSourcePath());
        writer.writeLine("Extraction : " + extractionId);
        writer.writeLine("Detect Code Location Type : " + codeLocation.getCodeLocationType());

        final DependencyGraph graph = codeLocation.getDependencyGraph();

        writer.writeLine("Root Dependencies : " + graph.getRootDependencies().size());
        writer.writeLine("Total Dependencies : " + dependencyCount);

    }

    private void writeBomToolCounts(final ReportWriter writer, final Map<DetectCodeLocationType, Integer> dependencyCounts) {
        for (final DetectCodeLocationType group : dependencyCounts.keySet()) {
            final Integer count = dependencyCounts.get(group);

            writer.writeLine(group.toString() + " : " + count);
        }
    }

}
