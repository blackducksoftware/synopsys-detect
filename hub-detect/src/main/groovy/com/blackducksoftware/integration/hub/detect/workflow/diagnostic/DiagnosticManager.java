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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;

public class DiagnosticManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectConfiguration detectConfiguration;
    private DiagnosticReportManager diagnosticReportManager;
    private DiagnosticLogManager diagnosticLogManager;
    private final DetectRun detectRun;
    private final FileManager fileManager;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;

    private boolean isDiagnosticProtected = false;
    private boolean isDiagnostic = false;
    private BomToolProfiler bomToolProfiler;

    public DiagnosticManager(final DetectConfiguration detectConfiguration,
        final DetectRun detectRun, final FileManager fileManager, final boolean isDiagnostic, final boolean isDiagnosticProtected, DirectoryManager directoryManager,
        final EventSystem eventSystem, final BomToolProfiler bomToolProfiler) {
        this.detectConfiguration = detectConfiguration;
        this.detectRun = detectRun;
        this.fileManager = fileManager;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
        this.bomToolProfiler = bomToolProfiler;

        init(isDiagnostic, isDiagnosticProtected);
    }

    private void init(final boolean isDiagnostic, final boolean isDiagnosticProtected) {

        this.isDiagnostic = isDiagnostic;
        this.isDiagnosticProtected = isDiagnosticProtected;

        if (!isDiagnostic) {
            return;
        }

        System.out.println();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("Diagnostic mode on.");
        System.out.println("A zip file will be created with logs and relevant detect output files.");
        System.out.println("It is not recommended to leave diagnostic mode on as you must manually clean up the zip.");
        if (!isDiagnosticProtected) {
            System.out.println("Additional relevant files such as lock files can be collected automatically in extended diagnostics.");
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println();

        logger.info("Initializing diagnostic managers.");
        try {
            diagnosticReportManager = new DiagnosticReportManager(directoryManager.getReportOutputDirectory(), detectRun.getRunId(), eventSystem, bomToolProfiler);
            diagnosticLogManager = new DiagnosticLogManager(directoryManager.getLogOutputDirectory(), eventSystem);
        } catch (final Exception e) {
            logger.error("Failed to process.", e);
        }

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
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None)) {
                //fileManager.cleanup(); //TODO: FIx
            }
        } else {
            logger.error("Diagnostic mode failed to create zip. Cleanup will not occur.");
        }

        logger.info("Diagnostic mode has completed.");
    }

    public boolean isDiagnosticModeOn() {
        return isDiagnostic;
    }

    private boolean createZip() {
        final List<File> directoriesToCompress = new ArrayList<>();
        directoriesToCompress.add(directoryManager.getRunHomeDirectory());

        final DiagnosticZipCreator zipper = new DiagnosticZipCreator();
        return zipper.createDiagnosticZip(detectRun.getRunId(), directoryManager.getRunsOutputDirectory(), directoriesToCompress);
    }

}
