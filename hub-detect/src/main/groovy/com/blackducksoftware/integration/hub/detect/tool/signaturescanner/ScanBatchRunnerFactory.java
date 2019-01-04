/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.OperatingSystemType;

public class ScanBatchRunnerFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntEnvironmentVariables intEnvironmentVariables;
    private final ExecutorService executorService;
    private final Slf4jIntLogger slf4jIntLogger;
    private final OperatingSystemType operatingSystemType;
    private final ScanPathsUtility scanPathsUtility;
    private final ScanCommandRunner scanCommandRunner;

    public ScanBatchRunnerFactory(IntEnvironmentVariables intEnvironmentVariables, ExecutorService executorService) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.executorService = executorService;
        slf4jIntLogger = new Slf4jIntLogger(logger);
        operatingSystemType = OperatingSystemType.determineFromSystem();
        scanPathsUtility = new ScanPathsUtility(slf4jIntLogger, intEnvironmentVariables, operatingSystemType);
        scanCommandRunner = new ScanCommandRunner(slf4jIntLogger, intEnvironmentVariables, scanPathsUtility, executorService);
    }

    public ScanBatchRunner withHubInstall(BlackDuckServerConfig hubServerConfig) {
        // will will use the hub server to download/update the scanner - this is the most likely situation
        ScannerZipInstaller scannerZipInstaller = ScannerZipInstaller.defaultUtility(slf4jIntLogger, hubServerConfig, scanPathsUtility, operatingSystemType);
        ScanBatchRunner scanBatchManager = ScanBatchRunner.createComplete(slf4jIntLogger, intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanCommandRunner);
        return scanBatchManager;
    }

    public ScanBatchRunner withoutInstall(File defaultInstallDirectory) {
        // either we were given an existing path for the scanner or
        // we are offline - either way, we won't attempt to manage the install
        return ScanBatchRunner.createWithNoInstaller(slf4jIntLogger, intEnvironmentVariables, defaultInstallDirectory, scanPathsUtility, scanCommandRunner);
    }

    public ScanBatchRunner withUserProvidedUrl(String userProvidedScannerInstallUrl, ConnectionManager connectionManager) throws DetectUserFriendlyException {
        // we will use the provided url to download/update the scanner
        final RestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(userProvidedScannerInstallUrl);
        final CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(slf4jIntLogger);
        final ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(slf4jIntLogger, restConnection, cleanupZipExpander, scanPathsUtility, userProvidedScannerInstallUrl, operatingSystemType);

        return ScanBatchRunner.createComplete(slf4jIntLogger, intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanCommandRunner);
    }

}
