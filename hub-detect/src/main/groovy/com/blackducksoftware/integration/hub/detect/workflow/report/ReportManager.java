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

import java.io.File;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolEvaluation;

public class ReportManager {
    // all entry points to reporting
    private final EventSystem eventSystem;
    private final PhoneHomeManager phoneHomeManager;
    private final DiagnosticManager diagnosticManager;

    // Summary, print collections or final groups or information.
    private final SearchSummaryReporter searchSummaryReporter;
    private final PreparationSummaryReporter preparationSummaryReporter;
    private final ExtractionSummaryReporter extractionSummaryReporter;

    private final InfoLogReportWriter logWriter = new InfoLogReportWriter();
    private final DebugLogReportWriter debugLogWriter = new DebugLogReportWriter();

    public ReportManager(final EventSystem eventSystem, final PhoneHomeManager phoneHomeManager, final DiagnosticManager diagnosticManager,
        final PreparationSummaryReporter preparationSummaryReporter, final ExtractionSummaryReporter extractionSummaryReporter, final SearchSummaryReporter searchSummaryReporter) {
        this.eventSystem = eventSystem;
        this.phoneHomeManager = phoneHomeManager;
        this.diagnosticManager = diagnosticManager;
        this.preparationSummaryReporter = preparationSummaryReporter;
        this.extractionSummaryReporter = extractionSummaryReporter;
        this.searchSummaryReporter = searchSummaryReporter;

        eventSystem.registerListener(Event.ExtractionStarted, it -> extractionStarted((BomToolEvaluation) it));
        eventSystem.registerListener(Event.ExtractionStarted, it -> extractionEnded((BomToolEvaluation) it));
    }

    // Reports
    public void searchCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        searchSummaryReporter.print(logWriter, bomToolEvaluations);
        final DetailedSearchSummaryReporter detailedSearchSummaryReporter = new DetailedSearchSummaryReporter();
        detailedSearchSummaryReporter.print(debugLogWriter, bomToolEvaluations);
    }

    public void preparationCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        preparationSummaryReporter.write(logWriter, bomToolEvaluations);
    }

    public void extractionsCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        //phoneHomeManager.startPhoneHome(bomToolProfiler.getAggregateBomToolGroupTimes());
        diagnosticManager.completedBomToolEvaluations(bomToolEvaluations);
    }

    public void codeLocationsCompleted(final List<BomToolEvaluation> bomToolEvaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        extractionSummaryReporter.writeSummary(logWriter, bomToolEvaluations, codeLocationNameMap);
        diagnosticManager.completedCodeLocations(bomToolEvaluations, codeLocationNameMap);
    }

    // Profiling
    private void extractionStarted(final BomToolEvaluation bomToolEvaluation) {
        diagnosticManager.startLoggingExtraction(bomToolEvaluation.getExtractionId());
    }

    private void extractionEnded(final BomToolEvaluation bomToolEvaluation) {
        if (diagnosticManager.isDiagnosticModeOn()) {
            final List<File> diagnosticFiles = bomToolEvaluation.getBomTool().getRelevantDiagnosticFiles();
            for (final File file : diagnosticFiles) {
                //TODO fix
                //diagnosticManager.registerFileOfInterest(bomToolEvaluation.getExtractionId(), file);
            }
        }
        diagnosticManager.stopLoggingExtraction(bomToolEvaluation.getExtractionId());
    }
}
