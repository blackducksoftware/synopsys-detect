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

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detect.workflow.search.result.DetectorEvaluation;

public class OverviewSummaryReporter {
    public void writeReport(final ReportWriter writer, final List<DetectorEvaluation> results) {
        final OverviewSummarizer summarizer = new OverviewSummarizer();

        final List<OverviewSummaryData> summaries = summarizer.summarize(results);

        writeSummaries(writer, summaries);
    }

    private void writeSummaries(final ReportWriter writer, final List<OverviewSummaryData> summaries) {
        writer.writeSeperator();
        for (final OverviewSummaryData data : summaries) {
            writer.writeLine("DIRECTORY: " + data.getDirectory());
            writer.writeLine("DETECTOR: " + data.getDetectorName());
            writer.writeLine("\tEXTRACTABLE: " + data.wasExtractable());
            writer.writeLine("\tEXTRACTED: " + data.wasExtracted());
            if (StringUtils.isNotBlank(data.getErrorReason())) {
                writer.writeLine("\tERROR: " + data.getErrorReason());
            }
            data.getAssociatedData().forEach((key, value) -> {
                writer.writeLine("\t" + key + ": " + value);
            });
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine("");
        writer.writeLine("");
    }

}
