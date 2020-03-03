/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.ConnectionFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
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

    public BlackDuckSignatureScannerTool(final BlackDuckSignatureScannerOptions signatureScannerOptions, final DetectContext detectContext) {
        this.signatureScannerOptions = signatureScannerOptions;
        this.detectContext = detectContext;
    }

    // TODO: Don't accept an Optional as a parameter.
    public SignatureScannerToolResult runScanTool(final BlackDuckRunData blackDuckRunData, final NameVersion projectNameVersion, final Optional<File> dockerTar) throws DetectUserFriendlyException {
        final DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        final ConnectionFactory connectionFactory = detectContext.getBean(ConnectionFactory.class);
        final DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        Optional<BlackDuckServerConfig> blackDuckServerConfig = Optional.empty();
        if (blackDuckRunData.isOnline() && blackDuckRunData.getBlackDuckServerConfig().isPresent()) {
            blackDuckServerConfig = blackDuckRunData.getBlackDuckServerConfig();
        }

        Optional<Path> localScannerInstallPath = signatureScannerOptions.getOfflineLocalScannerInstallPath();
        if (!localScannerInstallPath.isPresent()) {
            localScannerInstallPath = signatureScannerOptions.getOnlineLocalScannerInstallPath();
        }
        localScannerInstallPath.ifPresent(path -> logger.debug(String.format("Determined local scanner path: %s", path.toString())));

        final BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        final ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParallelProcessors());
        final IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

        final ScanBatchRunnerFactory scanBatchRunnerFactory = new ScanBatchRunnerFactory(intEnvironmentVariables, executorService);
        final ScanBatchRunner scanBatchRunner;
        File installDirectory = directoryManager.getPermanentDirectory();
        if (blackDuckServerConfig.isPresent() && StringUtils.isBlank(signatureScannerOptions.getUserProvidedScannerInstallUrl()) && !localScannerInstallPath.isPresent()) {
            logger.debug("Signature scanner will use the Black Duck server to download/update the scanner - this is the most likely situation.");
            scanBatchRunner = scanBatchRunnerFactory.withInstall(blackDuckServerConfig.get());
        } else {
            if (StringUtils.isNotBlank(signatureScannerOptions.getUserProvidedScannerInstallUrl())) {
                logger.debug("Signature scanner will use the provided url to download/update the scanner.");
                final IntHttpClient restConnection = connectionFactory.createConnection(signatureScannerOptions.getUserProvidedScannerInstallUrl(), new SilentIntLogger()); //TODO: Should this be silent?
                scanBatchRunner = scanBatchRunnerFactory.withUserProvidedUrl(signatureScannerOptions.getUserProvidedScannerInstallUrl(), restConnection);
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
            final BlackDuckSignatureScanner blackDuckSignatureScanner = detectContext.getBean(BlackDuckSignatureScanner.class, signatureScannerOptions, scanBatchRunner, blackDuckServerConfig.orElse(null));
            if (blackDuckServerConfig.isPresent()) {
                logger.debug("Signature scan is online.");
                // Since we are online, we need to calculate the notification task range to wait for code locations.
                // TODO: Don't call get() on an unchecked Optional.
                final CodeLocationCreationService codeLocationCreationService = blackDuckRunData.getBlackDuckServicesFactory().get().createCodeLocationCreationService();
                final NotificationTaskRange notificationTaskRange = codeLocationCreationService.calculateCodeLocationRange();
                final ScanBatchOutput scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
                final CodeLocationCreationData<ScanBatchOutput> codeLocationCreationData = new CodeLocationCreationData<>(notificationTaskRange, scanBatchOutput);
                return SignatureScannerToolResult.createOnlineResult(codeLocationCreationData);
            } else {
                logger.debug("Signature scan is offline.");
                // Since we are offline, we can just perform the scan actions.
                final ScanBatchOutput scanBatchOutput = blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
                return SignatureScannerToolResult.createOfflineResult(scanBatchOutput);
            }
        } catch (final IOException | IntegrationException e) {
            logger.error(String.format("Signature scan failed: %s", e.getMessage()));
            logger.debug("Signature scan error", e);
            return SignatureScannerToolResult.createFailureResult();
        } finally {
            executorService.shutdownNow();
        }
    }

}