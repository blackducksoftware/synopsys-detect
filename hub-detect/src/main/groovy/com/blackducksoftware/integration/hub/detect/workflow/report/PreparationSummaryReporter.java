/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public class PreparationSummaryReporter {
    public void write(final ReportWriter writer, final List<BomToolEvaluation> results) {
        final PreparationSummarizer summarizer = new PreparationSummarizer();
        final List<PreparationSummaryData> result = summarizer.summarize(results);
        writeSummary(writer, result);
    }

    private void writeSummary(final ReportWriter writer, final List<PreparationSummaryData> datas) {
        writer.writeLine();
        writer.writeLine();
        writer.writeHeader();
        writer.writeLine("Preparation for extraction");
        writer.writeHeader();
        for (final PreparationSummaryData data : datas) {
            writer.writeLine(data.getDirectory());
            if (data.getReady().size() > 0) {
                writer.writeLine("\t READY: " + data.getReady().stream()
                                                    .map(it -> it.getBomTool().getDescriptiveName())
                                                    .sorted()
                                                    .collect(Collectors.joining(", ")));
            }
            if (data.getFailed().size() > 0) {
                data.getFailed().stream()
                    .map(it -> "\tFAILED:" + it.getBomTool().getDescriptiveName() + " - " + it.getExtractabilityMessage())
                    .sorted()
                    .forEach(writer::writeLine);
            }
        }
        writer.writeHeader();
        writer.writeLine();
        writer.writeLine();
    }

}
