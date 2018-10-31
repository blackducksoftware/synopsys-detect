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
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.exception.EncryptionException;
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

    public void runScanTool(NameVersion projectNameVersion, Optional<File> dockerTar) throws EncryptionException, DetectUserFriendlyException {
        DetectConfiguration detectConfiguration = detectContext.getBean(DetectConfiguration.class);
        DetectConfigurationFactory detectConfigurationFactory = detectContext.getBean(DetectConfigurationFactory.class);
        ConnectionManager connectionManager = detectContext.getBean(ConnectionManager.class);
        Optional<HubServiceManager> hubServiceManager = Optional.ofNullable(detectContext.getBean(HubServiceManager.class));

        Optional<HubServerConfig> hubServerConfig = Optional.empty();
        if (hubServiceManager.isPresent()) {
            hubServerConfig = Optional.of(hubServiceManager.get().getHubServerConfig());
        }

        logger.info("Will run the signature scanner tool.");
        final String localScannerInstallPath = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, PropertyAuthority.None);
        final String userProvidedScannerInstallUrl = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, PropertyAuthority.None);

        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = detectConfigurationFactory.createBlackDuckSignatureScannerOptions();
        final ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParrallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();

        ScanJobManagerFactory scanJobManagerFactory = new ScanJobManagerFactory();
        ScanJobManager scanJobManager;
        if (hubServerConfig.isPresent() && StringUtils.isBlank(userProvidedScannerInstallUrl) && StringUtils.isBlank(localScannerInstallPath)) {
            // will will use the hub server to download/update the scanner - this is the most likely situation
            scanJobManager = scanJobManagerFactory.withHubInstall(hubServerConfig.get(), executorService, intEnvironmentVariables);
        } else {
            if (StringUtils.isNotBlank(userProvidedScannerInstallUrl)) {
                // we will use the provided url to download/update the scanner
                scanJobManager = scanJobManagerFactory.withUserProvidedUrl(userProvidedScannerInstallUrl, connectionManager, executorService, intEnvironmentVariables);
            } else {
                // either we were given an existing path for the scanner or
                // we are offline - either way, we won't attempt to manage the install
                scanJobManager = scanJobManagerFactory.withoutInstall(executorService, intEnvironmentVariables);
            }
        }

        BlackDuckSignatureScanner blackDuckSignatureScanner;
        if (hubServerConfig.isPresent()) {
            blackDuckSignatureScanner = detectContext.getBean(OnlineBlackDuckSignatureScanner.class, signatureScannerOptions, scanJobManager, hubServerConfig.get());
        } else {
            blackDuckSignatureScanner = detectContext.getBean(OnlineBlackDuckSignatureScanner.class, signatureScannerOptions, scanJobManager);
        }
        try {
            blackDuckSignatureScanner.performScanActions(projectNameVersion, dockerTar.orElse(null)); //TODO: get docker tar file.
        } catch (IOException | InterruptedException | IntegrationException e) {
            logger.info("Signature scan failed!");
        } finally {
            executorService.shutdownNow();
        }
    }

}
