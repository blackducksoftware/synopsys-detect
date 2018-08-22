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
package com.blackducksoftware.integration.hub.detect.workflow.hub;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.workflow.project.DetectProject;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.cli.CLIDownloadUtility;
import com.synopsys.integration.blackduck.cli.CLILocation;
import com.synopsys.integration.blackduck.cli.OfflineCLILocation;
import com.synopsys.integration.blackduck.cli.parallel.ParallelSimpleScanner;
import com.synopsys.integration.blackduck.cli.summary.ScanTargetOutput;
import com.synopsys.integration.blackduck.configuration.HubScanConfig;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.service.model.ProjectRequestBuilder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class OfflineScanner {
    private final Logger logger = LoggerFactory.getLogger(OfflineScanner.class);

    private final Gson gson;
    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;

    public OfflineScanner(final Gson gson, final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility) {
        this.gson = gson;
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
    }

    public List<ScanTargetOutput> offlineScan(final DetectProject detectProject, final HubScanConfig hubScanConfig, final String hubSignatureScannerOfflineLocalPath)
            throws IllegalArgumentException, IntegrationException, DetectUserFriendlyException, InterruptedException {
        final IntLogger intLogger = new Slf4jIntLogger(logger);

        final HubServerConfig hubServerConfig = new HubServerConfig(null, 0, (String) null, null, false);
        final List<ScanTargetOutput> scanTargetOutputs;

        final IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();
        intEnvironmentVariables.putAll(System.getenv());

        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();
        projectRequestBuilder.setProjectName(detectProject.getProjectName());
        projectRequestBuilder.setVersionName(detectProject.getProjectVersion());

        final ExecutorService executorService = Executors.newFixedThreadPool(detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS));
        try {
            final ParallelSimpleScanner parallelSimpleScanner = new ParallelSimpleScanner(intLogger, intEnvironmentVariables, gson, executorService);
            final CLILocation cliLocation;
            final String signatureScannerPath;

            if (StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
                cliLocation = new OfflineCLILocation(intLogger, new File(hubSignatureScannerOfflineLocalPath));
                signatureScannerPath = hubSignatureScannerOfflineLocalPath;
            } else {
                cliLocation = new CLILocation(intLogger, hubScanConfig.getCommonScanConfig().getToolsDir());
                signatureScannerPath = hubScanConfig.getCommonScanConfig().getToolsDir().getAbsolutePath();
            }

            boolean cliInstalledOkay = checkCliInstall(cliLocation, new SilentLogger());

            if (!cliInstalledOkay && StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL))) {
                installSignatureScannerFromUrl(intLogger, hubScanConfig);
                cliInstalledOkay = checkCliInstall(cliLocation, intLogger);
            }

            if (!cliInstalledOkay) {
                scanTargetOutputs = Collections.emptyList();
                logger.warn(String.format("The signature scanner is not correctly installed at %s", signatureScannerPath));
            } else {
                scanTargetOutputs = parallelSimpleScanner.executeScans(hubServerConfig, hubScanConfig, projectRequestBuilder.build(), cliLocation);
                if (logger.isInfoEnabled()) {
                    scanTargetOutputs.stream()
                            .filter(scanTargetOutput -> scanTargetOutput != null && scanTargetOutput.getDryRunFile() != null && scanTargetOutput.getDryRunFile().isFile())
                            .map(scanTargetOutput -> String.format("The dry run file for target '%s' can be found at %s", scanTargetOutput.getScanTarget(), scanTargetOutput.getDryRunFile().getAbsolutePath()))
                            .forEach(logger::info);
                }
            }
        } finally {
            executorService.shutdownNow();
        }

        return scanTargetOutputs;
    }

    private void installSignatureScannerFromUrl(final IntLogger intLogger, final HubScanConfig hubScanConfig) throws DetectUserFriendlyException {
        try {
            logger.info(String.format("Attempting to download the signature scanner from %s", detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL)));
            final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
            restConnectionBuilder.setBaseUrl(detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL));
            restConnectionBuilder.setTimeout(detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT));
            restConnectionBuilder.applyProxyInfo(detectConfigurationUtility.getHubProxyInfo());
            restConnectionBuilder.setLogger(intLogger);
            final RestConnection restConnection = restConnectionBuilder.build();
            final CLIDownloadUtility cliDownloadUtility = new CLIDownloadUtility(intLogger, restConnection);
            cliDownloadUtility.performInstallation(hubScanConfig.getCommonScanConfig().getToolsDir(), detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL), "unknown");

        } catch (final Exception e) {
            throw new DetectUserFriendlyException(
                    String.format("There was a problem downloading the signature scanner from %s: %s", detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL), e.getMessage()), e,
                    ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private boolean checkCliInstall(final CLILocation cliLocation, final IntLogger intLogger) {
        boolean cliInstalledOkay = false;
        try {
            cliInstalledOkay = cliLocation.getCLIExists(intLogger);
        } catch (final IOException e) {
            logger.error(String.format("Couldn't check the signature scanner install: %s", e.getMessage()));
        }

        return cliInstalledOkay;
    }

}
