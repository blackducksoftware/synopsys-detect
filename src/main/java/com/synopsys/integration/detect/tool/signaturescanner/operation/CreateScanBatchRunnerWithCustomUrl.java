/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.signaturescanner.operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.http.client.SignatureScannerClient;
import com.synopsys.integration.blackduck.keystore.KeyStoreHelper;
import com.synopsys.integration.blackduck.useragent.UserAgentItem;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.signaturescanner.FakeBlackDuckHttpClientWrapper;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.OperatingSystemType;

public class CreateScanBatchRunnerWithCustomUrl {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntEnvironmentVariables intEnvironmentVariables;
    private final SignatureScannerLogger slf4jIntLogger;
    private final OperatingSystemType operatingSystemType;
    private final ScanPathsUtility scanPathsUtility;
    private final ScanCommandRunner scanCommandRunner;

    public CreateScanBatchRunnerWithCustomUrl(final IntEnvironmentVariables intEnvironmentVariables, final SignatureScannerLogger slf4jIntLogger, final OperatingSystemType operatingSystemType,
        final ScanPathsUtility scanPathsUtility, final ScanCommandRunner scanCommandRunner) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.slf4jIntLogger = slf4jIntLogger;
        this.operatingSystemType = operatingSystemType;
        this.scanPathsUtility = scanPathsUtility;
        this.scanCommandRunner = scanCommandRunner;
    }

    public ScanBatchRunner createScanBatchRunner(String providedUrl, ConnectionFactory connectionFactory, DetectInfo detectInfo) throws DetectUserFriendlyException {
        // INTCMN-528: Should need install directory here and not when creating the jobs.
        logger.debug("Signature scanner will use the provided url to download/update the scanner.");
        HttpUrl baseUrl;
        try {
            baseUrl = new HttpUrl(providedUrl);
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException("User provided scanner install url could not be parsed: " + providedUrl, e, ExitCodeType.FAILURE_CONFIGURATION);
        }
        UserAgentItem solutionUserAgentItem = createSolutionUserAgentItem(detectInfo);
        IntHttpClient restConnection = connectionFactory.createConnection(providedUrl, new SilentIntLogger()); //TODO: Should this be silent?
        FakeBlackDuckHttpClientWrapper fakeBlackDuckHttpClient = new FakeBlackDuckHttpClientWrapper(restConnection, baseUrl, solutionUserAgentItem);

        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(slf4jIntLogger);
        SignatureScannerClient signatureScannerClient = new SignatureScannerClient(fakeBlackDuckHttpClient);
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(slf4jIntLogger);
        ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(slf4jIntLogger, signatureScannerClient, cleanupZipExpander, scanPathsUtility,
            keyStoreHelper, baseUrl, operatingSystemType);

        return ScanBatchRunner.createComplete(intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanCommandRunner);

    }

    private UserAgentItem createSolutionUserAgentItem(DetectInfo detectInfo) {
        String version = null;
        if (null != detectInfo) {
            version = detectInfo.getDetectVersion();
        }
        return new UserAgentItem(new NameVersion("synopsys_detect", version));
    }
}
