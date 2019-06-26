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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.ConnectionManager;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScannerTool {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScannerTool.class);
    private DetectContext detectContext;
    private BlackDuckSignatureScannerOptions signatureScannerOptions;

    public BlackDuckSignatureScannerTool(final BlackDuckSignatureScannerOptions signatureScannerOptions, DetectContext detectContext) {
        this.signatureScannerOptions = signatureScannerOptions;
        this.detectContext = detectContext;
    }

    public SignatureScannerToolResult runScanTool(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, Optional<File> dockerTar) throws DetectUserFriendlyException {
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        ConnectionManager connectionManager = detectContext.getBean(ConnectionManager.class);
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        Optional<BlackDuckServerConfig> blackDuckServerConfig = Optional.empty();
        if (blackDuckRunData.isOnline() && blackDuckRunData.getBlackDuckServerConfig().isPresent()) {
            blackDuckServerConfig = blackDuckRunData.getBlackDuckServerConfig();
        }

        String localScannerInstallPath = "";
        if (StringUtils.isNotBlank(signatureScannerOptions.getOfflineLocalScannerInstallPath())) {
            localScannerInstallPath = signatureScannerOptions.getOfflineLocalScannerInstallPath();
            logger.debug("Determined offline local scanner path: " + localScannerInstallPath);
        } else if (StringUtils.isNotBlank(signatureScannerOptions.getOnlineLocalScannerInstallPath())) {
            localScannerInstallPath = signatureScannerOptions.getOnlineLocalScannerInstallPath();
            logger.debug("Determined online local scanner path: " + localScannerInstallPath);
        }

        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        final ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

        ScanBatchRunnerFactory scanBatchRunnerFactory = new ScanBatchRunnerFactory(intEnvironmentVariables, executorService);
        ScanBatchRunner scanBatchRunner;
        File installDirectory = directoryManager.getPermanentDirectory();
        if (blackDuckServerConfig.isPresent() && StringUtils.isBlank(signatureScannerOptions.getUserProvidedScannerInstallUrl()) && StringUtils.isBlank(localScannerInstallPath)) {
            logger.debug("Signature scanner will use the Black Duck server to download/update the scanner - this is the most likely situation.");
            scanBatchRunner = scanBatchRunnerFactory.withInstall(blackDuckServerConfig.get());
        } else {
            if (StringUtils.isNotBlank(signatureScannerOptions.getUserProvidedScannerInstallUrl())) {
                logger.debug("Signature scanner will use the provided url to download/update the scanner.");
                scanBatchRunner = scanBatchRunnerFactory.withUserProvidedUrl(signatureScannerOptions.getUserProvidedScannerInstallUrl(), connectionManager);
            } else {
                logger.debug("Signature scanner either given an existing path for the scanner or is offline - either way, we won't attempt to manage the install.");
                if (StringUtils.isNotBlank(localScannerInstallPath)) {
                    logger.debug("Using provided path: " + localScannerInstallPath);
                    installDirectory = new File(localScannerInstallPath);
                } else {
                    logger.debug("Using default scanner path.");
                }
                scanBatchRunner = scanBatchRunnerFactory.withoutInstall(installDirectory);
            }
        }
        logger.debug("Determined install directory: " + installDirectory.getAbsolutePath());

        try {
            //When offline, server config is null, otherwise scanner is created the same way online/offline.
            BlackDuckSignatureScanner blackDuckSignatureScanner = detectContext.getBean(BlackDuckSignatureScanner.class, signatureScannerOptions, scanBatchRunner, blackDuckServerConfig.orElse(null));
            if (blackDuckServerConfig.isPresent()) {
                logger.debug("Signature scan is online.");
                //Since we are online, we need to calculate the notification task range to wait for code locations.
                CodeLocationCreationService codeLocationCreationService = blackDuckRunData.getBlackDuckServicesFactory().get().createCodeLocationCreationService();
                NotificationTaskRange notificationTaskRange = codeLocationCreationService.calculateCodeLocationRange();
                ScanBatchOutput scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
                CodeLocationCreationData<ScanBatchOutput> codeLocationCreationData = new CodeLocationCreationData<>(notificationTaskRange, scanBatchOutput);
                return SignatureScannerToolResult.createOnlineResult(codeLocationCreationData);
            } else {
                logger.debug("Signature scan is offline.");
                //Since we are offline, we can just perform the scan actions.
                ScanBatchOutput scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
                return SignatureScannerToolResult.createOfflineResult(scanBatchOutput);
            }
        } catch (IOException | IntegrationException e) {
            logger.error(String.format("Signature scan failed: %s", e.getMessage()));
            logger.debug("Signature scan error", e);
            return SignatureScannerToolResult.createFailureResult();
        } finally {
            executorService.shutdownNow();
        }
    }

}