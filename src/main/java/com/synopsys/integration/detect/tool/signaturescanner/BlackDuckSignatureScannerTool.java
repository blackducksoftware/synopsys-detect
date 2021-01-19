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
package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.OnlineBlackDuckRunData;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScannerTool {
    private final Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScannerTool.class);
    private final DetectContext detectContext;
    private final BlackDuckSignatureScannerOptions signatureScannerOptions;

    public BlackDuckSignatureScannerTool(BlackDuckSignatureScannerOptions signatureScannerOptions, DetectContext detectContext) {
        this.signatureScannerOptions = signatureScannerOptions;
        this.detectContext = detectContext;
    }

    // TODO: Don't accept an Optional as a parameter.
    public SignatureScannerToolResult runScanTool(BlackDuckRunData blackDuckRunData, NameVersion projectNameVersion, Optional<File> dockerTar) throws DetectUserFriendlyException {
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        ConnectionFactory connectionFactory = detectContext.getBean(ConnectionFactory.class);
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);
        CodeLocationNameGenerator codeLocationNameService = detectContext.getBean(CodeLocationNameGenerator.class);
        CodeLocationNameManager codeLocationNameManager = detectContext.getBean(CodeLocationNameManager.class, codeLocationNameService);

        BlackDuckServerConfig blackDuckServerConfig = null;
        OnlineBlackDuckRunData onlineBlackDuckRunData = null;
        if (blackDuckRunData.isOnline()) {
            onlineBlackDuckRunData = (OnlineBlackDuckRunData) blackDuckRunData;
            blackDuckServerConfig = onlineBlackDuckRunData.getBlackDuckServerConfig();
        }

        Optional<Path> localScannerInstallPath = signatureScannerOptions.getOfflineLocalScannerInstallPath();
        if (!localScannerInstallPath.isPresent()) {
            localScannerInstallPath = signatureScannerOptions.getOnlineLocalScannerInstallPath();
        }
        localScannerInstallPath.ifPresent(path -> logger.debug(String.format("Determined local scanner path: %s", path.toString())));

        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();

        ScanBatchRunnerFactory scanBatchRunnerFactory = new ScanBatchRunnerFactory(intEnvironmentVariables, executorService);
        ScanBatchRunner scanBatchRunner;
        File installDirectory = directoryManager.getPermanentDirectory();
        if (blackDuckServerConfig != null && !signatureScannerOptions.getUserProvidedScannerInstallUrl().isPresent() && !localScannerInstallPath.isPresent()) {
            logger.debug("Signature scanner will use the Black Duck server to download/update the scanner - this is the most likely situation.");
            scanBatchRunner = scanBatchRunnerFactory.withInstall(blackDuckServerConfig);
        } else {
            Optional<String> possiblyUserProvidedScannerInstallUrl = signatureScannerOptions.getUserProvidedScannerInstallUrl();
            if (possiblyUserProvidedScannerInstallUrl.isPresent()) {
                logger.debug("Signature scanner will use the provided url to download/update the scanner.");
                String providedUrl = possiblyUserProvidedScannerInstallUrl.get();
                IntHttpClient restConnection = connectionFactory.createConnection(providedUrl, new SilentIntLogger()); //TODO: Should this be silent?
                BlackDuckHttpClientWrapper fakeBlackDuckHttpClient = new BlackDuckHttpClientWrapper(restConnection);
                scanBatchRunner = scanBatchRunnerFactory.withUserProvidedUrl(providedUrl, fakeBlackDuckHttpClient);
            } else {
                logger.debug("Signature scanner either given an existing path for the scanner or is offline - either way, we won't attempt to manage the install.");
                if (localScannerInstallPath.isPresent()) {
                    logger.debug(String.format("Using provided path: %s", localScannerInstallPath));
                    installDirectory = localScannerInstallPath.get().toFile();
                } else {
                    logger.debug("Using default scanner path.");
                }
                scanBatchRunner = scanBatchRunnerFactory.withoutInstall(installDirectory);
            }
        }
        logger.debug(String.format("Determined install directory: %s", installDirectory.getAbsolutePath()));

        try {
            // When offline, server config is null, otherwise scanner is created the same way online/offline.
            BlackDuckSignatureScanner blackDuckSignatureScanner = detectContext.getBean(BlackDuckSignatureScanner.class, signatureScannerOptions, scanBatchRunner, blackDuckServerConfig, codeLocationNameManager);
            if (onlineBlackDuckRunData != null && blackDuckServerConfig != null) {
                logger.debug("Signature scan is online.");
                // Since we are online, we need to calculate the notification task range to wait for code locations.
                CodeLocationCreationService codeLocationCreationService = onlineBlackDuckRunData.getCodeLocationCreationService();
                NotificationTaskRange notificationTaskRange = codeLocationCreationService.calculateCodeLocationRange();
                ScanBatchOutput scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
                CodeLocationCreationData<ScanBatchOutput> codeLocationCreationData = new CodeLocationCreationData<>(notificationTaskRange, scanBatchOutput);
                return SignatureScannerToolResult.createOnlineResult(codeLocationCreationData);
            } else {
                logger.debug("Signature scan is offline.");
                // Since we are offline, we can just perform the scan actions.
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
