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
package com.blackducksoftware.integration.hub.detect.workflow.diagnostic;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.report.CodeLocationReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.DetailedSearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.FileReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.InfoLogReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.OverviewSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ProfilingReporter;
import com.blackducksoftware.integration.hub.detect.workflow.report.ReportWriter;
import com.blackducksoftware.integration.hub.detect.workflow.report.SearchSummaryReporter;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorEvaluation;

public class DiagnosticReportManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<ReportTypes, FileReportWriter> reportWriters = new HashMap<>();

    public enum ReportTypes {
        SEARCH("search_report", "Search Result Report", "A breakdown of detector searching by directory."),
        SEARCH_DETAILED("search_detailed_report", "Search Result Report", "A breakdown of detector searching by directory."),
        BOM_TOOL("bom_tool_report", "Bom DetectTool Report", "A breakdown of detector's that were applicable and their preparation and extraction results."),
        BOM_TOOL_PROFILE("bom_tool_profile_report", "Bom DetectTool Profile Report", "A breakdown of timing and profiling for all detectors."),
        CODE_LOCATIONS("code_location_report", "Code Location Report", "A breakdown of code locations created, their dependencies and status results."),
        DEPENDENCY_COUNTS("dependency_counts_report", "Dependency Count Report", "A breakdown of how many dependencies each detector group generated in their graphs.");

        String reportFileName;
        String reportTitle;
        String reportDescription;

        ReportTypes(final String reportFileName, final String reportTitle, final String reportDescription) {
            this.reportFileName = reportFileName;
            this.reportTitle = reportTitle;
            this.reportDescription = reportDescription;
        }

        String getReportFileName() {
            return reportFileName;
        }

        String getReportTitle() {
            return reportTitle;
        }

        String getReportDescription() {
            return reportDescription;
        }
    }

    private final BomToolProfiler bomToolProfiler;
    private final File reportDirectory;
    private final String runId;

    public DiagnosticReportManager(final File reportDirectory, final String runId, EventSystem eventSystem, final BomToolProfiler bomToolProfiler) {
        this.reportDirectory = reportDirectory;
        this.runId = runId;
        this.bomToolProfiler = bomToolProfiler;
        createReports();

        eventSystem.registerListener(Event.BomToolsComplete, event -> completedBomToolEvaluations(event.evaluatedDetectors));
        eventSystem.registerListener(Event.CodeLocationsCalculated, event -> completedCodeLocations(event.getCodeLocationNames()));
    }

    public void finish() {
        writeReports();

        closeReportWriters();
    }

    private List<DetectorEvaluation> completedDetectorEvaluations = null;

    public void completedBomToolEvaluations(final List<DetectorEvaluation> detectorEvaluations) {
        completedDetectorEvaluations = detectorEvaluations;
        try {
            final SearchSummaryReporter searchReporter = new SearchSummaryReporter();
            searchReporter.print(getReportWriter(ReportTypes.SEARCH), detectorEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write search report.", e);
        }

        try {
            final DetailedSearchSummaryReporter searchReporter = new DetailedSearchSummaryReporter();
            searchReporter.print(getReportWriter(ReportTypes.SEARCH_DETAILED), detectorEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write detailed search report.", e);
        }

        try {
            final OverviewSummaryReporter overviewSummaryReporter = new OverviewSummaryReporter();
            overviewSummaryReporter.writeReport(getReportWriter(ReportTypes.BOM_TOOL), detectorEvaluations);
        } catch (final Exception e) {
            logger.error("Failed to write detector report.", e);
        }
    }

    public void completedCodeLocations(final Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (completedDetectorEvaluations == null)
            return;

        try {
            final ReportWriter clWriter = getReportWriter(ReportTypes.CODE_LOCATIONS);
            final ReportWriter dcWriter = getReportWriter(ReportTypes.DEPENDENCY_COUNTS);
            final CodeLocationReporter clReporter = new CodeLocationReporter();
            clReporter.writeCodeLocationReport(clWriter, dcWriter, completedDetectorEvaluations, codeLocationNameMap);
        } catch (final Exception e) {
            logger.error("Failed to write code location report.", e);
        }
    }

    private void writeReports() {
        try {
            final ReportWriter profileWriter = getReportWriter(ReportTypes.BOM_TOOL_PROFILE);
            final ProfilingReporter reporter = new ProfilingReporter();
            reporter.writeReport(profileWriter, bomToolProfiler);
        } catch (final Exception e) {
            logger.error("Failed to write profiling report.", e);
        }
    }

    private void createReports() {
        for (final ReportTypes reportType : ReportTypes.values()) {
            try {
                createReportWriter(reportType);
            } catch (final Exception e) {
                logger.error("Failed to create report: " + reportType.toString(), e);
            }
        }
    }

    private ReportWriter createReportWriter(final ReportTypes reportType) {
        try {
            final File reportFile = new File(reportDirectory, reportType.getReportFileName() + ".txt");
            final FileReportWriter fileReportWriter = new FileReportWriter(reportFile, reportType.getReportTitle(), reportType.getReportDescription(), runId);
            reportWriters.put(reportType, fileReportWriter);
            logger.info("Created report file: " + reportFile.getPath());
            return fileReportWriter;
        } catch (final Exception e) {
            logger.error("Failed to create report writer: " + reportType.toString(), e);
        }
        return new InfoLogReportWriter();
    }

    public ReportWriter getReportWriter(final ReportTypes type) {
        if (reportWriters.containsKey(type)) {
            return reportWriters.get(type);
        } else {
            return createReportWriter(type);
        }
    }

    private void closeReportWriters() {
        for (final FileReportWriter writer : reportWriters.values()) {
            writer.finish();
        }
    }
}
