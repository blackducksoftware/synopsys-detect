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
package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.profiling.DetectorTimings;
import com.synopsys.integration.detect.workflow.report.CodeLocationReporter;
import com.synopsys.integration.detect.workflow.report.ConfigurationReporter;
import com.synopsys.integration.detect.workflow.report.DetailedSearchSummaryReporter;
import com.synopsys.integration.detect.workflow.report.OverviewSummaryReporter;
import com.synopsys.integration.detect.workflow.report.ProfilingReporter;
import com.synopsys.integration.detect.workflow.report.SearchSummaryReporter;
import com.synopsys.integration.detect.workflow.report.writer.FileReportWriter;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.report.writer.ReportWriter;
import com.synopsys.integration.detector.base.DetectorEvaluationTree;

public class DiagnosticReportHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Map<ReportTypes, FileReportWriter> reportWriters = new HashMap<>();

    public enum ReportTypes {
        SEARCH("search_report", "Search DetectResult Report", "A breakdown of detector searching by directory."),
        SEARCH_DETAILED("search_detailed_report", "Search DetectResult Report", "A breakdown of detector searching by directory."),
        DETECTOR("detector_report", "Detector Report", "A breakdown of detector's that were applicableChildren and their preparation and extraction results."),
        DETECTOR_PROFILE("detector_profile_report", "Detector Profile Report", "A breakdown of timing and profiling for all detectors."),
        CODE_LOCATIONS("code_location_report", "Code Location Report", "A breakdown of code locations created, their dependencies and status results."),
        DEPENDENCY_COUNTS("dependency_counts_report", "Dependency Count Report", "A breakdown of how many dependencies each detector group generated in their graphs."),
        CONFIGURATION("detect_configuration", "Detect Configuration Report", "A complete set of all parameters detect used, including detect run and version.");

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

    private final File reportDirectory;
    private final String runId;

    public DiagnosticReportHandler(final File reportDirectory, final String runId, final EventSystem eventSystem) {
        this.reportDirectory = reportDirectory;
        this.runId = runId;
        createReports();

        eventSystem.registerListener(Event.DetectorsComplete, this::completedBomToolEvaluations);
        eventSystem.registerListener(Event.DetectCodeLocationNamesCalculated, event -> completedCodeLocations(event.getCodeLocationNames()));
        eventSystem.registerListener(Event.DetectorsProfiled, this::detectorsProfiled);
    }

    public void finish() {
        closeReportWriters();
    }

    private DetectorToolResult detectorToolResult;

    public void completedBomToolEvaluations(final DetectorToolResult detectorToolResult) {
        this.detectorToolResult = detectorToolResult;

        final DetectorEvaluationTree rootEvaluation;
        if (detectorToolResult.getRootDetectorEvaluationTree().isPresent()) {
            rootEvaluation = detectorToolResult.getRootDetectorEvaluationTree().get();
        } else {
            logger.warn("Detectors completed, but no evaluation was found, unable to write detector reports.");
            return;
        }

        try {
            final SearchSummaryReporter searchReporter = new SearchSummaryReporter();
            searchReporter.print(getReportWriter(ReportTypes.SEARCH), rootEvaluation);
        } catch (final Exception e) {
            logger.error("Failed to write search report.", e);
        }

        try {
            final DetailedSearchSummaryReporter searchReporter = new DetailedSearchSummaryReporter();
            searchReporter.print(getReportWriter(ReportTypes.SEARCH_DETAILED), rootEvaluation);
        } catch (final Exception e) {
            logger.error("Failed to write detailed search report.", e);
        }

        try {
            final OverviewSummaryReporter overviewSummaryReporter = new OverviewSummaryReporter();
            overviewSummaryReporter.writeReport(getReportWriter(ReportTypes.DETECTOR), rootEvaluation);
        } catch (final Exception e) {
            logger.error("Failed to write detector report.", e);
        }
    }

    public void completedCodeLocations(final Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (detectorToolResult == null || !detectorToolResult.getRootDetectorEvaluationTree().isPresent()) {
            return;
        }

        try {
            final ReportWriter clWriter = getReportWriter(ReportTypes.CODE_LOCATIONS);
            final ReportWriter dcWriter = getReportWriter(ReportTypes.DEPENDENCY_COUNTS);
            final CodeLocationReporter clReporter = new CodeLocationReporter();
            clReporter.writeCodeLocationReport(clWriter, dcWriter, detectorToolResult.getRootDetectorEvaluationTree().get(), detectorToolResult.getCodeLocationMap(), codeLocationNameMap);
        } catch (final Exception e) {
            logger.error("Failed to write code location report.", e);
        }
    }

    private void detectorsProfiled(final DetectorTimings detectorTimings) {
        try {
            final ReportWriter profileWriter = getReportWriter(ReportTypes.DETECTOR_PROFILE);
            final ProfilingReporter reporter = new ProfilingReporter();
            reporter.writeReport(profileWriter, detectorTimings);
        } catch (final Exception e) {
            logger.error("Failed to write profiling report.", e);
        }
    }

    public void configurationsReport(final DetectInfo detectInfo, final PropertyConfiguration propertyConfiguration, SortedMap<String, String> maskedRawPropertyValues, Set<String> propertyKeys) {
        try {
            final ReportWriter profileWriter = getReportWriter(ReportTypes.CONFIGURATION);
            final ConfigurationReporter reporter = new ConfigurationReporter();
            reporter.writeReport(profileWriter, detectInfo, propertyConfiguration, maskedRawPropertyValues, propertyKeys);
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
