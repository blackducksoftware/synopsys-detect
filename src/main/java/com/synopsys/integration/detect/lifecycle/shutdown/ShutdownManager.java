/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import javax.swing.text.html.Option;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class ShutdownManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void shutdown(Optional<ProductRunData> productRunData, Optional<DetectConfiguration> detectConfigurationOptional, Optional<DirectoryManager> directoryManagerOptional, Optional<DiagnosticSystem> diagnosticSystem) {

        if (productRunData.isPresent() && productRunData.get().shouldUseBlackDuckProduct()) {
            stopPhoneHome(productRunData.get());
        }

        diagnosticSystem.ifPresent(DiagnosticSystem::finish);

        if (detectConfigurationOptional.isPresent() && directoryManagerOptional.isPresent()) {
            DetectConfiguration detectConfiguration = detectConfigurationOptional.get();
            DirectoryManager directoryManager = directoryManagerOptional.get();
            cleanupRun(productRunData, directoryManager, detectConfiguration);
        }
    }

    private void stopPhoneHome(ProductRunData productRunData) {
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

    private void cleanupRun(Optional<ProductRunData> productRunData, DirectoryManager directoryManager, DetectConfiguration detectConfiguration) {
        try {
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None)) {
                logger.info("Detect will cleanup.");
                boolean dryRun = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, PropertyAuthority.None);

                boolean offline = false;
                if (productRunData.isPresent() && productRunData.get().shouldUseBlackDuckProduct()) {
                    BlackDuckRunData blackDuckRunData = productRunData.get().getBlackDuckRunData();
                    if (!blackDuckRunData.isOnline()) {
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
    }

    private void cleanup(File directory, List<File> skip) throws IOException {
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

        if (directory.listFiles().length == 0) {
            logger.info("Cleaning up directory: " + directory.getAbsolutePath());
            FileUtils.forceDelete(directory);
        }

        if (null != exception) {
            throw exception;
        }
    }

}
