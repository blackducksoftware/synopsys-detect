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
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.workflow.DetectProjectManager;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.ExceptionBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.BomToolProfiler;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;

public class ExtractionManager {
    private final Logger logger = LoggerFactory.getLogger(DetectProjectManager.class);

    private final PreparationSummaryReporter preparationSummaryReporter;
    private final ExtractionReporter extractionReporter;
    private final BomToolProfiler bomToolProfiler;
    private final DiagnosticManager diagnosticManager;

    public ExtractionManager(final PreparationSummaryReporter preparationSummaryReporter, final ExtractionReporter extractionReporter, final BomToolProfiler bomToolProfiler, final DiagnosticManager diagnosticManager) {
        this.preparationSummaryReporter = preparationSummaryReporter;
        this.extractionReporter = extractionReporter;
        this.bomToolProfiler = bomToolProfiler;
        this.diagnosticManager = diagnosticManager;
    }

    private int extractions = 0;

    private void extract(final List<BomToolEvaluation> results) {
        final List<BomToolEvaluation> extractable = results.stream().filter(result -> result.isExtractable()).collect(Collectors.toList());

        for (int i = 0; i < extractable.size(); i++) {
            logger.info("Extracting " + Integer.toString(i + 1) + " of " + Integer.toString(extractable.size()) + " (" + Integer.toString((int) Math.floor((i * 100.0f) / extractable.size())) + "%)");
            extract(extractable.get(i));
        }
    }

    private void prepare(final List<BomToolEvaluation> results) {
        for (final BomToolEvaluation result : results) {
            prepare(result);
        }
    }

    private void prepare(final BomToolEvaluation result) {
        if (result.isApplicable()) {
            bomToolProfiler.extractableStarted(result.getBomTool());
            try {
                result.setExtractable(result.getBomTool().extractable());
            } catch (final Exception e) {
                result.setExtractable(new ExceptionBomToolResult(e));
            }
            bomToolProfiler.extractableEnded(result.getBomTool());
        }
    }

    private void extract(final BomToolEvaluation result) {
        if (result.isExtractable()) {
            extractions++;
            final ExtractionId extractionId = new ExtractionId(result.getBomTool().getBomToolGroupType(), Integer.toString(extractions));
            extractionReporter.startedExtraction(result.getBomTool(), extractionId);
            bomToolProfiler.extractionStarted(result.getBomTool());
            try {
                result.setExtraction(result.getBomTool().extract(extractionId));
            } catch (final Exception e) {

            }
            if (diagnosticManager.isDiagnosticModeOn()) {
                final List<File> diagnosticFiles = result.getBomTool().getRelevantDiagnosticFiles();
                for (final File file : diagnosticFiles) {
                    diagnosticManager.registerFileOfInterest(extractionId, file);
                }
            }
            bomToolProfiler.extractionEnded(result.getBomTool());
            extractionReporter.endedExtraction(result.getExtraction());
        }

    }

    public ExtractionResult performExtractions(final List<BomToolEvaluation> bomToolEvaluations) throws IntegrationException, DetectUserFriendlyException {
        prepare(bomToolEvaluations);

        preparationSummaryReporter.print(bomToolEvaluations);

        extract(bomToolEvaluations);

        final HashSet<BomToolGroupType> succesfulBomToolGroups = new HashSet<>();
        final HashSet<BomToolGroupType> failedBomToolGroups = new HashSet<>();
        for (final BomToolEvaluation evaluation : bomToolEvaluations) {
            final BomToolGroupType type = evaluation.getBomTool().getBomToolGroupType();
            if (evaluation.isApplicable()) {
                if (evaluation.isExtractable() && evaluation.wasExtractionSuccessful()) {
                    succesfulBomToolGroups.add(type);
                } else {
                    failedBomToolGroups.add(type);
                }
            }
        }

        final List<DetectCodeLocation> codeLocations = bomToolEvaluations.stream()
                .filter(it -> it.wasExtractionSuccessful())
                .flatMap(it -> it.getExtraction().codeLocations.stream())
                .collect(Collectors.toList());

        final ExtractionResult result = new ExtractionResult(codeLocations, succesfulBomToolGroups, failedBomToolGroups);
        return result;
    }

}
