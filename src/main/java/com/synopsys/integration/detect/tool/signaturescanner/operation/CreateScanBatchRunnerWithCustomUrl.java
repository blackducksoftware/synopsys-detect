package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.http.client.SignatureScannerClient;
import com.synopsys.integration.blackduck.keystore.KeyStoreHelper;
import com.synopsys.integration.blackduck.service.dataservice.BlackDuckRegistrationService;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerLogger;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.OperatingSystemType;

public class CreateScanBatchRunnerWithCustomUrl {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntEnvironmentVariables intEnvironmentVariables;
    private final SignatureScannerLogger slf4jIntLogger;
    private final OperatingSystemType operatingSystemType;
    private final ScanPathsUtility scanPathsUtility;
    private final ScanCommandRunner scanCommandRunner;

    public CreateScanBatchRunnerWithCustomUrl(
        IntEnvironmentVariables intEnvironmentVariables,
        SignatureScannerLogger slf4jIntLogger,
        OperatingSystemType operatingSystemType,
        ScanPathsUtility scanPathsUtility,
        ScanCommandRunner scanCommandRunner
    ) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.slf4jIntLogger = slf4jIntLogger;
        this.operatingSystemType = operatingSystemType;
        this.scanPathsUtility = scanPathsUtility;
        this.scanCommandRunner = scanCommandRunner;
    }

    public ScanBatchRunner createScanBatchRunner(String providedUrl, ConnectionDetails connectionDetails, BlackDuckRegistrationService registrationService, File installDirectory)
        throws DetectUserFriendlyException {
        logger.debug("Signature scanner will use the provided url to download/update the scanner.");
        HttpUrl baseUrl;
        try {
            baseUrl = new HttpUrl(providedUrl);
        } catch (IntegrationException e) {
            throw new DetectUserFriendlyException("User provided scanner install url could not be parsed: " + providedUrl, e, ExitCodeType.FAILURE_CONFIGURATION);
        }

        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(slf4jIntLogger);
        SignatureScannerClient signatureScannerClient = new SignatureScannerClient(
            new SilentIntLogger(),
            connectionDetails.getGson(),
            connectionDetails.getTimeout().intValue(),
            connectionDetails.getAlwaysTrust(),
            connectionDetails.getProxyInformation()
        );
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(slf4jIntLogger);
        ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(
            slf4jIntLogger,
            signatureScannerClient,
            registrationService,
            cleanupZipExpander,
            scanPathsUtility,
            keyStoreHelper,
            baseUrl,
            operatingSystemType,
            installDirectory
        );

        return ScanBatchRunner.createComplete(intEnvironmentVariables, scanPathsUtility, scanCommandRunner, scannerZipInstaller);

    }

}
