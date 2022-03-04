package com.synopsys.integration.detect.tool.signaturescanner.operation;

import java.io.File;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.http.client.SignatureScannerClient;
import com.synopsys.integration.blackduck.keystore.KeyStoreHelper;
import com.synopsys.integration.blackduck.service.dataservice.BlackDuckRegistrationService;
import com.synopsys.integration.detect.tool.signaturescanner.SignatureScannerLogger;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.OperatingSystemType;

public class CreateScanBatchRunnerWithBlackDuck {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntEnvironmentVariables intEnvironmentVariables;
    private final OperatingSystemType operatingSystemType;
    private final ExecutorService executorService;

    public CreateScanBatchRunnerWithBlackDuck(IntEnvironmentVariables intEnvironmentVariables, OperatingSystemType operatingSystemType, ExecutorService executorService) {
        this.intEnvironmentVariables = intEnvironmentVariables;
        this.operatingSystemType = operatingSystemType;
        this.executorService = executorService;
    }

    public ScanBatchRunner createScanBatchRunner(BlackDuckServerConfig blackDuckServerConfig, File installDirectory) {
        logger.debug("Signature scanner will use the Black Duck server to download/update the scanner - this is the most likely situation.");
        SignatureScannerLogger slf4jIntLogger = new SignatureScannerLogger(logger);
        ScanPathsUtility scanPathsUtility = new ScanPathsUtility(slf4jIntLogger, intEnvironmentVariables, operatingSystemType);
        ScanCommandRunner scanCommandRunner = new ScanCommandRunner(slf4jIntLogger, intEnvironmentVariables, scanPathsUtility, executorService);
        BlackDuckHttpClient blackDuckHttpClient = blackDuckServerConfig.createBlackDuckHttpClient(slf4jIntLogger);
        CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(slf4jIntLogger);
        SignatureScannerClient signatureScannerClient = new SignatureScannerClient(blackDuckHttpClient);
        BlackDuckRegistrationService blackDuckRegistrationService = blackDuckServerConfig.createBlackDuckServicesFactory(slf4jIntLogger).createBlackDuckRegistrationService();
        KeyStoreHelper keyStoreHelper = new KeyStoreHelper(slf4jIntLogger);
        ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(
            slf4jIntLogger,
            signatureScannerClient,
            blackDuckRegistrationService,
            cleanupZipExpander,
            scanPathsUtility,
            keyStoreHelper,
            blackDuckServerConfig.getBlackDuckUrl(),
            operatingSystemType,
            installDirectory
        );
        return ScanBatchRunner.createComplete(intEnvironmentVariables, scanPathsUtility, scanCommandRunner, scannerZipInstaller);
    }

}
