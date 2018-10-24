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

        eventSystem.registerListener(Event.SearchCompleted, event -> searchCompleted(event.getBomToolEvaluations()));
        eventSystem.registerListener(Event.PreparationsCompleted, event -> preparationsCompleted(event.getBomToolEvaluations()));
        eventSystem.registerListener(Event.BomToolsComplete, event -> bomToolsComplete(event.evaluatedBomTools));
        eventSystem.registerListener(Event.CodeLocationsCalculated, event -> codeLocationsCompleted(event.getCodeLocationNames()));

    }

    // Reports
    public void searchCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        searchSummaryReporter.print(logWriter, bomToolEvaluations);
        final DetailedSearchSummaryReporter detailedSearchSummaryReporter = new DetailedSearchSummaryReporter();
        detailedSearchSummaryReporter.print(debugLogWriter, bomToolEvaluations);
    }

    public void preparationsCompleted(final List<BomToolEvaluation> bomToolEvaluations) {
        preparationSummaryReporter.write(logWriter, bomToolEvaluations);
    }

    private List<BomToolEvaluation> completedBomToolEvaluations = new ArrayList<>();

    public void bomToolsComplete(final List<BomToolEvaluation> bomToolEvaluations) {
        preparationSummaryReporter.write(logWriter, bomToolEvaluations);
        completedBomToolEvaluations.addAll(bomToolEvaluations);
    }

    public void codeLocationsCompleted(final Map<DetectCodeLocation, String> codeLocationNameMap) {
        extractionSummaryReporter.writeSummary(logWriter, completedBomToolEvaluations, codeLocationNameMap);
    }
}
