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

import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public class ErrorSummaryReporter {

    public void writeSummary(ReportWriter writer, final List<BomToolEvaluation> results) {
        final ErrorSummarizer summarizer = new ErrorSummarizer();

        final List<ErrorSummaryData> summaries = summarizer.summarize(results);

        writeSummaries(writer, summaries);
    }

    private void writeSummaries(ReportWriter writer, final List<ErrorSummaryData> data) {
        boolean willPrintAtLeastOne = data.stream().filter(it -> it.getException().size() > 0 || it.getFailed().size() > 0 || it.getNotExtractable().size() > 0).count() > 0;
        if (!willPrintAtLeastOne)
            return;

        ReporterUtils.printHeader(writer, "Detector Issue Summary");
        data.stream().forEach(it -> {
            if (it.getException().size() > 0 || it.getFailed().size() > 0 || it.getNotExtractable().size() > 0) {
                writer.writeLine(it.getDirectory());
                String spacer = "\t\t\t";
                writeEvaluationsIfNotEmpty(writer, "\tNot Extractable: ", spacer, it.getNotExtractable());
                writeEvaluationsIfNotEmpty(writer, "\t        Failure: ", spacer, it.getFailed());
                writeEvaluationsIfNotEmpty(writer, "\t      Exception: ", spacer, it.getException());
            }
        });
        ReporterUtils.printFooter(writer);
    }

    private void writeEvaluationsIfNotEmpty(final ReportWriter writer, final String prefix, final String spacer, final List<ErrorSummaryBomToolError> evaluations) {
        if (evaluations.size() > 0) {
            evaluations.stream().forEach(evaluation -> {
                writer.writeLine(prefix + evaluation.getBomToolName());
                writer.writeLine(spacer + evaluation.getReason());
            });
        }
    }

}
