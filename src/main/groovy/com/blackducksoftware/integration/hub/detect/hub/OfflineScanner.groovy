/*
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
package com.blackducksoftware.integration.hub.detect.hub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.cli.CLIDownloadUtility
import com.blackducksoftware.integration.hub.cli.CLILocation
import com.blackducksoftware.integration.hub.cli.OfflineCLILocation
import com.blackducksoftware.integration.hub.cli.SimpleScanUtility
import com.blackducksoftware.integration.hub.configuration.HubScanConfig
import com.blackducksoftware.integration.hub.configuration.HubServerConfig
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType
import com.blackducksoftware.integration.hub.detect.model.DetectProject
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnectionBuilder
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.SilentLogger
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.util.CIEnvironmentVariables
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class OfflineScanner {
    private static final Logger logger = LoggerFactory.getLogger(OfflineScanner.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    Gson gson

    void offlineScan(DetectProject detectProject, HubScanConfig hubScanConfig, String hubSignatureScannerOfflineLocalPath) {
        def intLogger = new Slf4jIntLogger(logger)

        def hubServerConfig = new HubServerConfig(null, 0, (String)null, null, false)


        def ciEnvironmentVariables = new CIEnvironmentVariables()
        ciEnvironmentVariables.putAll(System.getenv())

        def silentLogger = new SilentLogger()

        def simpleScanUtility = new SimpleScanUtility(intLogger, gson, hubServerConfig, ciEnvironmentVariables, hubScanConfig, detectProject.projectName, detectProject.projectVersionName)
        final CLILocation cliLocation = new CLILocation(silentLogger, hubScanConfig.getToolsDir())
        if (hubSignatureScannerOfflineLocalPath) {
            cliLocation = new OfflineCLILocation(silentLogger, new File(hubSignatureScannerOfflineLocalPath))
        }

        boolean cliInstalledOkay = checkCliInstall(cliLocation, silentLogger)
        if (!cliInstalledOkay && detectConfiguration.hubSignatureScannerHostUrl) {
            installSignatureScannerFromUrl(intLogger, hubScanConfig, ciEnvironmentVariables);
            cliInstalledOkay = checkCliInstall(cliLocation, silentLogger)
        }

        if (!cliInstalledOkay && hubSignatureScannerOfflineLocalPath) {
            logger.warn("The signature scanner is not correctly installed at ${hubSignatureScannerOfflineLocalPath}")
        } else if (!cliInstalledOkay) {
            logger.warn("The signature scanner is not correctly installed at ${hubScanConfig.getToolsDir()}")
        } else {
            simpleScanUtility.setupAndExecuteScan(cliLocation)
            logger.info("The scan dry run files can be found in : ${simpleScanUtility.getDataDirectory()}")
        }
    }

    private void installSignatureScannerFromUrl(IntLogger intLogger, HubScanConfig hubScanConfig, CIEnvironmentVariables ciEnvironmentVariables) {
        try {
            logger.info("Attempting to download the signature scanner from ${detectConfiguration.hubSignatureScannerHostUrl}")
            UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder()
            restConnectionBuilder.setBaseUrl(detectConfiguration.hubSignatureScannerHostUrl)
            restConnectionBuilder.setTimeout(detectConfiguration.hubTimeout)
            restConnectionBuilder.applyProxyInfo(detectConfiguration.getHubProxyInfo())
            restConnectionBuilder.setLogger(intLogger)
            RestConnection restConnection = restConnectionBuilder.build()
            CLIDownloadUtility cliDownloadUtility = new CLIDownloadUtility(intLogger, restConnection)
            cliDownloadUtility.performInstallation(hubScanConfig.getToolsDir(), ciEnvironmentVariables, detectConfiguration.hubSignatureScannerHostUrl, 'unknown', 'hub-detect')
        } catch (Exception e) {
            throw new DetectUserFriendlyException("There was a problem downloading the signature scanner from ${detectConfiguration.hubSignatureScannerHostUrl}: ${e.message}", e, ExitCodeType.FAILURE_GENERAL_ERROR)
        }
    }

    private boolean checkCliInstall(CLILocation cliLocation, IntLogger silentLogger) {
        boolean cliInstalledOkay = false
        try {
            cliInstalledOkay = cliLocation.getCLIExists(silentLogger)
        } catch (IOException e) {
            logger.error("Couldn't check the signature scanner install: ${e.message}")
        }

        return cliInstalledOkay
    }
}
