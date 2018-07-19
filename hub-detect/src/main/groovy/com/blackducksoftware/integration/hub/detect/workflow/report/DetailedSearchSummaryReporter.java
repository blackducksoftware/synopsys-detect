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

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class DetailedSearchSummaryReporter {
    public void print(final ReportWriter writer, final List<BomToolEvaluation> results) {
        final DetailedSearchSummarizer detailedSearchSummarizer = new DetailedSearchSummarizer();
        final List<DetailedSearchSummaryData> detailedSearchData = detailedSearchSummarizer.summarize(results);

        printDirectoriesDebug(writer, detailedSearchData);
    }

    private void printDirectoriesDebug(final ReportWriter writer, final List<DetailedSearchSummaryData> detailedSearchData) {
        for (final DetailedSearchSummaryData data : detailedSearchData) {
            final List<String> toPrint = new ArrayList<>();
            toPrint.addAll(printDetails(writer, "      APPLIED: ", data.getApplicable()));
            toPrint.addAll(printDetails(writer, "DID NOT APPLY: ", data.getNotApplicable()));
            toPrint.addAll(printDetails(writer, "DID NOT APPLY: ", data.getNotSearchable()));

            if (toPrint.size() > 0) {
                writer.writeSeperator();
                writer.writeLine("Detailed search results for directory");
                writer.writeLine(data.getDirectory());
                writer.writeSeperator();
                toPrint.stream().sorted().forEach(it -> writer.writeLine(it));
                writer.writeSeperator();
            }
        }
    }

    private List<String> printDetails(final ReportWriter writer, final String prefix, final List<DetailedSearchSummaryBomToolData> details) {
        final List<String> toPrint = new ArrayList<>();
        for (final DetailedSearchSummaryBomToolData detail : details) {
            toPrint.add(prefix + detail.getBomTool().getDescriptiveName() + ": " + detail.getReason());
        }
        return toPrint;
    }
}
