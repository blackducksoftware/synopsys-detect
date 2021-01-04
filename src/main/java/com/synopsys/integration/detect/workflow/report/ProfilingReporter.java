/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synopsys.integration.detect.workflow.profiling.DetectorTimings;
import com.synopsys.integration.detect.workflow.profiling.Timing;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class ProfilingReporter {
    public void writeReport(final ReportWriter writer, final DetectorTimings detectorTimings) {
        writer.writeSeparator();
        writer.writeLine("Applicable Times");
        writer.writeSeparator();
        writeAggregateReport(writer, detectorTimings.getApplicableTimings());
        writer.writeSeparator();
        writer.writeLine("Extractable Times");
        writer.writeSeparator();
        writeReport(writer, detectorTimings.getExtractableTimings());
        writer.writeSeparator();
        writer.writeLine("Discovery Times");
        writer.writeSeparator();
        writeReport(writer, detectorTimings.getDiscoveryTimings());
        writer.writeLine("Extraction Times");
        writer.writeSeparator();
        writeReport(writer, detectorTimings.getExtractionTimings());
    }

    private void writeAggregateReport(final ReportWriter writer, final List<Timing<DetectorEvaluation>> timings) {
        final Map<String, Long> aggregated = new HashMap<>();

        for (final Timing<DetectorEvaluation> detectorTime : timings) {
            final String name = detectorTime.getKey().getDetectorRule().getDescriptiveName();
            if (!aggregated.containsKey(name)) {
                aggregated.put(name, 0L);
            }
            aggregated.put(name, aggregated.get(name) + detectorTime.getMs());
        }

        for (final Map.Entry<String, Long> aggregatedEntry : aggregated.entrySet()) {
            writer.writeLine("\t" + padToLength(aggregatedEntry.getKey(), 30) + "\t" + aggregatedEntry.getValue());
        }
    }

    private void writeReport(final ReportWriter writer, final List<Timing<DetectorEvaluation>> timings) {
        for (final Timing<DetectorEvaluation> detectorTime : timings) {
            writer.writeLine("\t" + padToLength(detectorTime.getKey().getDetectorRule().getDescriptiveName(), 30) + "\t" + detectorTime.getMs());
        }

    }

    private String padToLength(final String text, final int length) {
        String outText = text;
        while (outText.length() < length) {
            outText += " ";
        }
        return outText;
    }
}
