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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class DiagnosticManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectConfiguration detectConfiguration;

    private File outputDirectory;

    private final DiagnosticReportManager diagnosticReportManager;
    private final DiagnosticLogManager diagnosticLogManager;
    private final DetectRunManager detectRunManager;
    private final DiagnosticFileManager diagnosticFileManager;

    private boolean isDiagnosticProtected = false;
    private boolean isDiagnostic = false;

    public DiagnosticManager(final DetectConfiguration detectConfiguration, final DiagnosticReportManager diagnosticReportManager, final DiagnosticLogManager diagnosticLogManager,
            final DetectRunManager detectRunManager, final DiagnosticFileManager diagnosticFileManager) {
        this.detectConfiguration = detectConfiguration;
        this.diagnosticReportManager = diagnosticReportManager;
        this.diagnosticLogManager = diagnosticLogManager;
        this.detectRunManager = detectRunManager;
        this.diagnosticFileManager = diagnosticFileManager;
    }

    public void init(final boolean isDiagnostic, final boolean isDiagnosticProtected) {

        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticProtected = isDiagnosticProtected;

        if (!isDiagnostic) {
            return;
        }

        System.out.println("");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Diagnostic mode on. Run id " + detectRunManager.getRunId());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("");

        final File bdioDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH));
        this.outputDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_OUTPUT_PATH));
        try {
            diagnosticFileManager.init(outputDirectory, bdioDirectory, detectRunManager.getRunId());
        } catch (final Exception e) {
            logger.error("Failed to create diagnostics directory.", e);
        }

        logger.info("Initializing diagnostic managers.");
        try {
            diagnosticReportManager.init(diagnosticFileManager.getReportDirectory(), detectRunManager.getRunId());
            diagnosticLogManager.init(diagnosticFileManager.getLogDirectory());
        } catch (final Exception e) {
            logger.error("Failed to initialize.", e);
        }

        logger.info("Diagnostic mode on. Run id " + detectRunManager.getRunId());
    }

    public void finish() {
        if (!isDiagnosticModeOn()) {
            return;
        }
        logger.info("Finishing diagnostic mode.");

        try {
            logger.info("Finishing reports.");
            diagnosticReportManager.finish();
        } catch (final Exception e) {
            logger.error("Failed to finish.", e);
        }

        try {
            logger.info("Finishing logging.");
            diagnosticLogManager.finish();
        } catch (final Exception e) {
            logger.error("Failed to finish.", e);
        }

        logger.info("Creating diagnostics zip.");
        boolean zipCreated = false;
        try {
            zipCreated = createZip();
        } catch (final Exception e) {
            logger.error("Failed to create diagnostic zip. Cleanup will not occur.", e);
        }

        if (zipCreated) {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP)) {
                diagnosticFileManager.cleanup();
            }
        } else {
            logger.error("Diagnostic mode failed to create zip. Cleanup will not occur.");
        }

        logger.info("Diagnostic mode has completed.");
    }

    public boolean isDiagnosticModeOn() {
        return isDiagnostic;
    }

    /*
     * If this returns true, customer files or anything related to customer source should NOT be collected during diagnostics. Otherwise, things like lock files, solutions files, build reports may be collected during diagnostics.
     */
    public boolean isProtectedModeOn() {
        return isDiagnosticProtected;
    }

    public boolean shouldFileManagerCleanup() {
        if (isDiagnosticModeOn()) {
            return false;
        }

        return true;
    }

    public void registerFileOfInterest(final ExtractionId extractionId, final File file) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        if (isProtectedModeOn()) {
            return;
        }
        diagnosticFileManager.registerFileOfInterest(extractionId, file);
    }

    public void registerGlobalFileOfInterest(final File file) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        if (isProtectedModeOn()) {
            return;
        }
        diagnosticFileManager.registerGlobalFileOfInterest(file);
    }

    public void startLoggingExtraction(final ExtractionId extractionId) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticLogManager.startLoggingExtraction(extractionId);
    }

    public void stopLoggingExtraction(final ExtractionId extractionId) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticLogManager.stopLoggingExtraction(extractionId);
    }

    public void completedBomToolEvaluations(final List<BomToolEvaluation> evaluations) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticReportManager.completedBomToolEvaluations(evaluations);
    }

    public void completedCodeLocations(final List<BomToolEvaluation> evaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        if (!isDiagnosticModeOn()) {
            return;
        }
        diagnosticReportManager.completedCodeLocations(evaluations, codeLocationNameMap);
    }

    private boolean createZip() {
        final List<File> directoriesToCompress = diagnosticFileManager.getAllDirectories().stream()
                .filter(it -> it.exists())
                .collect(Collectors.toList());

        final DiagnosticZipCreator zipper = new DiagnosticZipCreator();
        return zipper.createDiagnosticZip(detectRunManager.getRunId(), outputDirectory, directoriesToCompress);
    }

}
