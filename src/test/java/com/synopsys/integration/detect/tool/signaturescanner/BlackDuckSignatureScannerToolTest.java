package com.synopsys.integration.detect.tool.signaturescanner;

import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.BlackDuckOnlineProperties;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommand;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.http.BlackDuckRequestFactory;
import com.synopsys.integration.blackduck.http.client.ApiTokenBlackDuckHttpClient;
import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScannerToolTest {

    @Test
    public void testRunScanToolOffline() throws URISyntaxException, DetectUserFriendlyException, IOException, IntegrationException {
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = new BlackDuckSignatureScannerOptions(null, null, null, null, null, null, null, 1, null, null, false, null, null, null, null, null, null, null);
        File signatureScannerInstallationDirectory = new File(BlackDuckSignatureScannerToolTest.class.getClassLoader().getResource("tool/signaturescanner/tools").toURI());
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, null, signatureScannerInstallationDirectory.toPath());
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, new DetectRun(""));

        BlackDuckSignatureScanner blackDuckSignatureScanner = Mockito.mock(BlackDuckSignatureScanner.class);
        DetectContext detectContext = Mockito.mock(DetectContext.class);
        Mockito.when(detectContext.getBean(any(Class.class), any(BlackDuckSignatureScannerOptions.class), any(ScanBatchRunner.class), any(), any())).thenReturn(blackDuckSignatureScanner);

        NameVersion projectNameVersion = new NameVersion("name", "version");
        BlackDuckOnlineProperties blackDuckOnlineProperties = new BlackDuckOnlineProperties(null, false, false, false);
        ScanCommand scanCommand = new ScanCommand(null, null, false, null, null, 0, null, null, null, null, null, 0, false, null, blackDuckOnlineProperties, null, null, null, null, false, false, null, null);
        ScanBatchOutput scanBatchOutput = new ScanBatchOutput(Collections.singletonList(ScanCommandOutput.SUCCESS(projectNameVersion, null, null, scanCommand, null)));
        Mockito.when(blackDuckSignatureScanner.performScanActions(projectNameVersion, signatureScannerInstallationDirectory, null)).thenReturn(scanBatchOutput);

        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(new CodeLocationNameGenerator(null));
        BlackDuckSignatureScannerTool1 blackDuckSignatureScannerTool1 = new BlackDuckSignatureScannerTool1(blackDuckSignatureScannerOptions, detectContext, directoryManager, codeLocationNameManager, null);

        SignatureScannerToolResult expected = SignatureScannerToolResult.createOfflineResult(scanBatchOutput);
        SignatureScannerToolResult actual = blackDuckSignatureScannerTool1.runScanTool(BlackDuckRunData.offline(), projectNameVersion, Optional.empty());
        Assertions.assertTrue(areEqualResults(expected, actual));
    }

    @Test
    public void testRunScanToolOnline() {
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        BlackDuckHttpClient blackDuckHttpClient = getBlackDuckHttpClient();
        IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));
        BlackDuckRequestFactory blackDuckRequestFactory = new BlackDuckRequestFactory();
        BlackDuckServicesFactory blackDuckServicesFactory = new BlackDuckServicesFactory(intEnvironmentVariables, gson, objectMapper, executorService, blackDuckHttpClient, logger, blackDuckRequestFactory);
        BlackDuckRunData blackDuckRunData = BlackDuckRunData.online();
    }

    private BlackDuckHttpClient getBlackDuckHttpClient() {
        return new ApiTokenBlackDuckHttpClient();
    }

    private boolean areEqualResults(SignatureScannerToolResult result1, SignatureScannerToolResult result2) {
        boolean equalCreationData = (!result1.getCreationData().isPresent() && !result2.getCreationData().isPresent()) ||
                                        result1.getCreationData().isPresent() && result2.getCreationData().isPresent()
                                        && result1.getCreationData().get().equals(result2.getCreationData().get());
        boolean equalOutputs = result1.getScanBatchOutput().equals(result2.getScanBatchOutput());
        boolean equalResults = result1.getResult().equals(result2.getResult());

        return equalCreationData && equalOutputs && equalResults;
    }
}
