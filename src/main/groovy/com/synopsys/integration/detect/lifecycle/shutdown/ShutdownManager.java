/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.lifecycle.shutdown;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.tool.detector.RequiredDetectorChecker;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.report.ReportManager;
import com.synopsys.integration.detect.workflow.status.DetectStatusManager;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectStatusManager detectStatusManager;
    private final ExitCodeManager exitCodeManager;
    private final DirectoryManager directoryManager;
    private final DetectConfiguration detectConfiguration;
    private final ReportManager reportManager;
    private final DiagnosticManager diagnosticManager;
    private final ProductRunData productRunData;

    public ShutdownManager(ProductRunData productRunData, DetectStatusManager detectStatusManager, final ExitCodeManager exitCodeManager,
        final DirectoryManager directoryManager, final DetectConfiguration detectConfiguration, ReportManager reportManager, DiagnosticManager diagnosticManager) {
        this.detectStatusManager = detectStatusManager;
        this.exitCodeManager = exitCodeManager;
        this.directoryManager = directoryManager;
        this.detectConfiguration = detectConfiguration;
        this.reportManager = reportManager;
        this.diagnosticManager = diagnosticManager;
        this.productRunData = productRunData;
    }

    public void shutdown(Optional<RunResult> runResultOptional) {
        if (productRunData.shouldUseBlackDuckProduct()){
            BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
            if (blackDuckRunData.getPhoneHomeManager().isPresent()) {
                try {
                    logger.debug("Ending phone home.");
                    blackDuckRunData.getPhoneHomeManager().get().endPhoneHome();
                } catch (final Exception e) {
                    logger.debug(String.format("Error trying to end the phone home task: %s", e.getMessage()));
                }
            }
        }

        try {
            if (diagnosticManager.getDiagnosticSystem().isPresent()) {
                logger.debug("Ending diagnostics.");
                diagnosticManager.getDiagnosticSystem().get().finish();
            }
        } catch (final Exception e) {
            logger.debug(String.format("Error trying to finish diagnostics: %s", e.getMessage()));
        }

        try {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None)) {
                logger.info("Detect will cleanup.");
                boolean dryRun = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, PropertyAuthority.None);

                boolean offline = false;
                if (productRunData.shouldUseBlackDuckProduct()){
                    BlackDuckRunData blackDuckRunData = productRunData.getBlackDuckRunData();
                    if (!blackDuckRunData.isOnline()){
                        offline = true;
                    }
                }


                List<File> cleanupToSkip = new ArrayList<>();
                if (dryRun || offline) {
                    logger.debug("Will not cleanup scan folder.");
                    cleanupToSkip.add(directoryManager.getScanOutputDirectory());
                }
                if (offline) {
                    logger.debug("Will not cleanup bdio folder.");
                    cleanupToSkip.add(directoryManager.getBdioOutputDirectory());
                }
                logger.debug("Cleaning up directory: " + directoryManager.getRunHomeDirectory().getAbsolutePath());
                cleanup(directoryManager.getRunHomeDirectory(), cleanupToSkip);
            } else {
                logger.info("Skipping cleanup, it is disabled.");
            }
        } catch (final Exception e) {
            logger.debug(String.format("Error trying cleanup: %s", e.getMessage()));
        }

        Set<DetectorType> detectorTypes = new HashSet<>();
        if (runResultOptional.isPresent()) {
            detectorTypes.addAll(runResultOptional.get().getApplicableDetectors());
        }

        //Check required detector types
        String requiredDetectors = detectConfiguration.getProperty(DetectProperty.DETECT_REQUIRED_DETECTOR_TYPES, PropertyAuthority.None);
        RequiredDetectorChecker requiredDetectorChecker = new RequiredDetectorChecker();
        RequiredDetectorChecker.RequiredDetectorResult requiredDetectorResult = requiredDetectorChecker.checkForMissingDetectors(requiredDetectors, detectorTypes);
        if (requiredDetectorResult.wereDetectorsMissing()) {
            String missingDetectors = requiredDetectorResult.getMissingDetectors().stream().map(it -> it.toString()).collect(Collectors.joining(","));
            logger.error("One or more required detector types were not found: " + missingDetectors);
            exitCodeManager.requestExitCode(ExitCodeType.FAILURE_DETECTOR_REQUIRED);
        }
    }

    public void cleanup(File directory, List<File> skip) throws IOException {
        IOException exception = null;
        for (final File file : directory.listFiles()) {
            try {
                if (skip.contains(file)) {
                    logger.debug("Skipping cleanup for: " + file.getAbsolutePath());
                } else {
                    logger.debug("Cleaning up: " + file.getAbsolutePath());
                    FileUtils.forceDelete(file);
                }
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception) {
            throw exception;
        }
    }

}
