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

public class SearchSummaryReporter {
    public void print(final ReportWriter writer, final List<BomToolEvaluation> results) {
        final SearchSummarizer searchSummarizer = new SearchSummarizer();
        final List<SearchSummaryData> summaryData = searchSummarizer.summarize(results);

        printDirectoriesInfo(writer, summaryData);
    }

    private void printDirectoriesInfo(final ReportWriter writer, final List<SearchSummaryData> summaryData) {
        writer.writeLine();
        writer.writeLine();
        writer.writeHeader();
        writer.writeLine("Search results");
        writer.writeHeader();
        for (final SearchSummaryData data : summaryData) {
            writer.writeLine(data.getDirectory());
            writer.writeLine("\tAPPLIES: " + data.getApplicable().stream().map(it -> it.getDescriptiveName()).sorted().collect(Collectors.joining(", ")));
        }
        writer.writeLine(ReportConstants.HEADING);
        writer.writeLine();
        writer.writeLine();
    }

}
