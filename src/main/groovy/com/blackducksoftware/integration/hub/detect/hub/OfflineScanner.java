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
package com.blackducksoftware.integration.hub.detect.hub;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.cli.CLIDownloadUtility;
import com.blackducksoftware.integration.hub.cli.CLILocation;
import com.blackducksoftware.integration.hub.cli.OfflineCLILocation;
import com.blackducksoftware.integration.hub.cli.parallel.ParallelSimpleScanner;
import com.blackducksoftware.integration.hub.cli.summary.ScanTargetOutput;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.RestConnection;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.util.IntEnvironmentVariables;
import com.google.gson.Gson;

@Component
public class OfflineScanner {
    private final Logger logger = LoggerFactory.getLogger(OfflineScanner.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private Gson gson;

    public List<ScanTargetOutput> offlineScan(final DetectProject detectProject, final HubScanConfig hubScanConfig, final String hubSignatureScannerOfflineLocalPath)
            throws IllegalArgumentException, IntegrationException, DetectUserFriendlyException, InterruptedException {
        final IntLogger intLogger = new Slf4jIntLogger(logger);

        final HubServerConfig hubServerConfig = new HubServerConfig(null, 0, (String) null, null, false);

        final IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();
        intEnvironmentVariables.putAll(System.getenv());

        final ProjectRequestBuilder projectRequestBuilder = new ProjectRequestBuilder();
        projectRequestBuilder.setProjectName(detectProject.getProjectName());
        projectRequestBuilder.setVersionName(detectProject.getProjectVersion());

        final ParallelSimpleScanner parallelSimpleScanner = new ParallelSimpleScanner(intLogger, intEnvironmentVariables, gson);
        CLILocation cliLocation = new CLILocation(intLogger, hubScanConfig.getToolsDir());
        if (StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            cliLocation = new OfflineCLILocation(intLogger, new File(hubSignatureScannerOfflineLocalPath));
        }

        boolean cliInstalledOkay = checkCliInstall(cliLocation, intLogger);
        if (!cliInstalledOkay && StringUtils.isNotBlank(detectConfiguration.getHubSignatureScannerHostUrl())) {
            installSignatureScannerFromUrl(intLogger, hubScanConfig);
            cliInstalledOkay = checkCliInstall(cliLocation, intLogger);
        }

        if (!cliInstalledOkay && StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            logger.warn(String.format("The signature scanner is not correctly installed at %s", hubSignatureScannerOfflineLocalPath));
            return Collections.emptyList();
        } else if (!cliInstalledOkay) {
            logger.warn(String.format("The signature scanner is not correctly installed at %s", hubScanConfig.getToolsDir()));
            return Collections.emptyList();
        } else {
            final List<ScanTargetOutput> scanTargetOutputs = parallelSimpleScanner.executeScans(hubServerConfig, hubScanConfig, projectRequestBuilder.build(), cliLocation, detectConfiguration.getHubSignatureScannerParallelProcessors());
            if (null != scanTargetOutputs && !scanTargetOutputs.isEmpty()) {
                for (final ScanTargetOutput scanTargetOutput : scanTargetOutputs) {
                    if (null != scanTargetOutput && null != scanTargetOutput.getDryRunFile() && scanTargetOutput.getDryRunFile().isFile()) {
                        logger.info(String.format("The dry run file for target '%s' can be found at : %s", scanTargetOutput.getScanTarget(), scanTargetOutput.getDryRunFile().getAbsolutePath()));
                    }
                }
            }
            return scanTargetOutputs;
        }
    }

    private void installSignatureScannerFromUrl(final IntLogger intLogger, final HubScanConfig hubScanConfig) throws DetectUserFriendlyException {
        try {
            logger.info(String.format("Attempting to download the signature scanner from %s", detectConfiguration.getHubSignatureScannerHostUrl()));
            final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
            restConnectionBuilder.setBaseUrl(detectConfiguration.getHubSignatureScannerHostUrl());
            restConnectionBuilder.setTimeout(detectConfiguration.getHubTimeout());
            restConnectionBuilder.applyProxyInfo(detectConfiguration.getHubProxyInfo());
            restConnectionBuilder.setLogger(intLogger);
            final RestConnection restConnection = restConnectionBuilder.build();
            final CLIDownloadUtility cliDownloadUtility = new CLIDownloadUtility(intLogger, restConnection);
            cliDownloadUtility.performInstallation(hubScanConfig.getToolsDir(), detectConfiguration.getHubSignatureScannerHostUrl(), "unknown");
        } catch (final Exception e) {
            throw new DetectUserFriendlyException(String.format("There was a problem downloading the signature scanner from %s: %s", detectConfiguration.getHubSignatureScannerHostUrl(), e.getMessage()), e,
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
