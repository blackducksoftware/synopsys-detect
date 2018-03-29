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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.cli.CLIDownloadUtility;
import com.blackducksoftware.integration.hub.cli.CLILocation;
import com.blackducksoftware.integration.hub.cli.OfflineCLILocation;
import com.blackducksoftware.integration.hub.cli.SimpleScanUtility;
import com.blackducksoftware.integration.hub.configuration.HubScanConfig;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.hub.rest.UriCombiner;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;
import com.google.gson.Gson;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class OfflineScanner {
    private static final Logger logger = LoggerFactory.getLogger(OfflineScanner.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private Gson gson;

    boolean offlineScan(final DetectProject detectProject, final HubScanConfig hubScanConfig, final String hubSignatureScannerOfflineLocalPath)
            throws IllegalArgumentException, IntegrationException, DetectUserFriendlyException, InterruptedException {
        final IntLogger intLogger = new Slf4jIntLogger(OfflineScanner.logger);

        final HubServerConfig hubServerConfig = new HubServerConfig(null, 0, (String) null, null, false, new UriCombiner());

        final CIEnvironmentVariables ciEnvironmentVariables = new CIEnvironmentVariables();
        ciEnvironmentVariables.putAll(System.getenv());

        final SimpleScanUtility simpleScanUtility = new SimpleScanUtility(intLogger, gson, hubServerConfig, ciEnvironmentVariables, hubScanConfig, detectProject.getProjectName(), detectProject.getProjectVersionName());
        CLILocation cliLocation = new CLILocation(intLogger, hubScanConfig.getToolsDir());
        if (StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            cliLocation = new OfflineCLILocation(intLogger, new File(hubSignatureScannerOfflineLocalPath));
        }

        boolean cliInstalledOkay = checkCliInstall(cliLocation, intLogger);
        if (!cliInstalledOkay && StringUtils.isNotBlank(detectConfiguration.getHubSignatureScannerHostUrl())) {
            installSignatureScannerFromUrl(intLogger, hubScanConfig, ciEnvironmentVariables);
            cliInstalledOkay = checkCliInstall(cliLocation, intLogger);
        }

        if (!cliInstalledOkay && StringUtils.isNotBlank(hubSignatureScannerOfflineLocalPath)) {
            OfflineScanner.logger.warn(String.format("The signature scanner is not correctly installed at %s", hubSignatureScannerOfflineLocalPath));
            return false;
        } else if (!cliInstalledOkay) {
            OfflineScanner.logger.warn(String.format("The signature scanner is not correctly installed at %s", hubScanConfig.getToolsDir()));
            return false;
        } else {
            simpleScanUtility.setupAndExecuteScan(cliLocation);
            OfflineScanner.logger.info(String.format("The scan dry run files can be found in : %s", simpleScanUtility.getDataDirectory()));
            return true;
        }
    }

    private void installSignatureScannerFromUrl(final IntLogger intLogger, final HubScanConfig hubScanConfig, final CIEnvironmentVariables ciEnvironmentVariables) throws DetectUserFriendlyException {
        try {
            OfflineScanner.logger.info(String.format("Attempting to download the signature scanner from %s", detectConfiguration.getHubSignatureScannerHostUrl()));
            final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
            restConnectionBuilder.setBaseUrl(detectConfiguration.getHubSignatureScannerHostUrl());
            restConnectionBuilder.setTimeout(detectConfiguration.getHubTimeout());
            restConnectionBuilder.applyProxyInfo(detectConfiguration.getHubProxyInfo());
            restConnectionBuilder.setLogger(intLogger);
            final RestConnection restConnection = restConnectionBuilder.build();
            final CLIDownloadUtility cliDownloadUtility = new CLIDownloadUtility(intLogger, restConnection);
            cliDownloadUtility.performInstallation(hubScanConfig.getToolsDir(), ciEnvironmentVariables, detectConfiguration.getHubSignatureScannerHostUrl(), "unknown", "hub-detect");
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
            OfflineScanner.logger.error(String.format("Couldn't check the signature scanner install: %s", e.getMessage()));
        }

        return cliInstalledOkay;
    }

}
