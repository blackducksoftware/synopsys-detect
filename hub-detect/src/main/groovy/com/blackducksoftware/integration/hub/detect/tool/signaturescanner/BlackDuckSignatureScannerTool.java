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
package com.blackducksoftware.integration.hub.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.ConnectivityManager;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchManager;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
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

    public void runScanTool(NameVersion projectNameVersion, Optional<File> dockerTar) throws DetectUserFriendlyException {
        DetectConfiguration detectConfiguration = detectContext.getBean(DetectConfiguration.class);
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        ConnectionManager connectionManager = detectContext.getBean(ConnectionManager.class);
        ConnectivityManager connectivityManager = detectContext.getBean(ConnectivityManager.class);
        DirectoryManager directoryManager = detectContext.getBean(DirectoryManager.class);

        Optional<HubServerConfig> hubServerConfig = Optional.empty();
        if (connectivityManager.isDetectOnline() && connectivityManager.getHubServiceManager().isPresent()) {
            hubServerConfig = Optional.of(connectivityManager.getHubServiceManager().get().getHubServerConfig());
        }

        logger.info("Will run the signature scanner tool.");
        final String offlineLocalScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None);
        final String onlineLocalScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_LOCAL_PATH, PropertyAuthority.None);

        String localScannerInstallPath = "";
        if (StringUtils.isNotBlank(offlineLocalScannerInstallPath)) {
            localScannerInstallPath = offlineLocalScannerInstallPath;
            logger.debug("Determined offline local scanner path: " + localScannerInstallPath);
        } else if (StringUtils.isNotBlank(onlineLocalScannerInstallPath)) {
            localScannerInstallPath = onlineLocalScannerInstallPath;
            logger.debug("Determined online local scanner path: " + localScannerInstallPath);
        }

        final String userProvidedScannerInstallUrl = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None);

        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        final ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParrallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

        ScanBatchManagerFactory scanJobManagerFactory = new ScanBatchManagerFactory();
        ScanBatchManager scanJobManager;
        File installDirectory = directoryManager.getPermanentDirectory();
        if (hubServerConfig.isPresent() && StringUtils.isBlank(userProvidedScannerInstallUrl) && StringUtils.isBlank(localScannerInstallPath)) {
            logger.debug("Signature scanner will use the hub server to download/update the scanner - this is the most likely situation.");
            scanJobManager = scanJobManagerFactory.withHubInstall(hubServerConfig.get(), executorService, intEnvironmentVariables);
        } else {
            if (StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
                logger.debug("Signature scanner will use the provided url to download/update the scanner.");
                scanJobManager = scanJobManagerFactory.withUserProvidedUrl(userProvidedScannerInstallUrl, connectionManager, executorService, intEnvironmentVariables);
            } else {
                logger.debug("Signature scanner either given an existing path for the scanner or is offline - either way, we won't attempt to manage the install.");
                installDirectory = new File(localScannerInstallPath);
                scanJobManager = scanJobManagerFactory.withoutInstall(installDirectory, executorService, intEnvironmentVariables);
            }
        }
        logger.debug("Determined install directory: " + installDirectory.getAbsolutePath());

        BlackDuckSignatureScanner blackDuckSignatureScanner;
        if (hubServerConfig.isPresent()) {
            logger.debug("Signature scan is online.");
            blackDuckSignatureScanner = detectContext.getBean(OnlineBlackDuckSignatureScanner.class, signatureScannerOptions, scanJobManager, hubServerConfig.get());
        } else {
            logger.debug("Signature scan is offline.");
            blackDuckSignatureScanner = detectContext.getBean(OfflineBlackDuckSignatureScanner.class, signatureScannerOptions, scanJobManager);
        }
        try {
            blackDuckSignatureScanner.performScanActions(projectNameVersion, installDirectory, dockerTar.orElse(null));
        } catch (IOException | InterruptedException | IntegrationException e) {
            logger.info("Signature scan failed!");
            logger.debug("Signature scan error!", e);
        } finally {
            executorService.shutdownNow();
        }
    }

}
