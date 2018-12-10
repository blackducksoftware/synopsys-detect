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

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchManager;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.OperatingSystemType;

public class ScanJobManagerFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger);

    public ScanBatchManager withHubInstall(BlackDuckServerConfig hubServerConfig, ExecutorService executorService, IntEnvironmentVariables intEnvironmentVariables) {
        // will will use the hub server to download/update the scanner - this is the most likely situation
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        ScanPathsUtility scanPathsUtility = createScanPathsUtility(intEnvironmentVariables, operatingSystemType);
        ScanCommandRunner scanCommandRunner = createScanCommandRunner(intEnvironmentVariables, scanPathsUtility, executorService);

        ScannerZipInstaller scannerZipInstaller = ScannerZipInstaller.defaultUtility(slf4jIntLogger, hubServerConfig, scanPathsUtility, operatingSystemType);
        ScanBatchManager scanJobManager = ScanBatchManager.createFullScanManager(slf4jIntLogger, intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanCommandRunner);
        return scanJobManager;
    }

    public ScanBatchManager withoutInstall(ExecutorService executorService, IntEnvironmentVariables intEnvironmentVariables) {
        // either we were given an existing path for the scanner or
        // we are offline - either way, we won't attempt to manage the install
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        ScanPathsUtility scanPathsUtility = createScanPathsUtility(intEnvironmentVariables, operatingSystemType);
        ScanCommandRunner scanCommandRunner = createScanCommandRunner(intEnvironmentVariables, scanPathsUtility, executorService);

        //FIXME need to specify the default install directory
        return ScanBatchManager.createScanManagerWithNoInstaller(slf4jIntLogger, intEnvironmentVariables, null, scanPathsUtility, scanCommandRunner);
    }

    public ScanBatchManager withUserProvidedUrl(String userProvidedScannerInstallUrl, ConnectionManager connectionManager, ExecutorService executorService, IntEnvironmentVariables intEnvironmentVariables)
        throws DetectUserFriendlyException {
        // we will use the provided url to download/update the scanner
        OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        ScanPathsUtility scanPathsUtility = createScanPathsUtility(intEnvironmentVariables, operatingSystemType);
        ScanCommandRunner scanCommandRunner = createScanCommandRunner(intEnvironmentVariables, scanPathsUtility, executorService);

        final RestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(userProvidedScannerInstallUrl);
        final CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(slf4jIntLogger);
        final ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(slf4jIntLogger, restConnection, cleanupZipExpander, scanPathsUtility, userProvidedScannerInstallUrl, operatingSystemType);

        return ScanBatchManager.createFullScanManager(slf4jIntLogger, intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanCommandRunner);
    }

    private ScanPathsUtility createScanPathsUtility(IntEnvironmentVariables intEnvironmentVariables, OperatingSystemType operatingSystemType) {
        return new ScanPathsUtility(slf4jIntLogger, intEnvironmentVariables, operatingSystemType);

    }

    private ScanCommandRunner createScanCommandRunner(IntEnvironmentVariables intEnvironmentVariables, ScanPathsUtility scanPathsUtility, ExecutorService executorService) {
        return new ScanCommandRunner(slf4jIntLogger, intEnvironmentVariables, scanPathsUtility, executorService);
    }
}
