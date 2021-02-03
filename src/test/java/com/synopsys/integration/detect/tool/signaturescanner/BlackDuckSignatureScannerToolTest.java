package com.synopsys.integration.detect.tool.signaturescanner;

import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synopsys.integration.blackduck.api.generated.discovery.BlackDuckMediaTypeDiscovery;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationService;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.BlackDuckOnlineProperties;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommand;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.model.NotificationTaskRange;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.DetectContext;
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
    public void testRunScanTool() throws URISyntaxException, DetectUserFriendlyException, IOException, IntegrationException {
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = new BlackDuckSignatureScannerOptions(null, null, null, null, null, null, null, 1, null, null, false, null, null, null, null, null, null, null);

        File signatureScannerInstallationDirectory = new File(BlackDuckSignatureScannerToolTest.class.getClassLoader().getResource("tool/signaturescanner/tools").toURI());
        DirectoryOptions directoryOptions = new DirectoryOptions(null, null, null, null, signatureScannerInstallationDirectory.toPath());
        DirectoryManager directoryManager = new DirectoryManager(directoryOptions, new DetectRun(""));

        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(new CodeLocationNameGenerator(null));

        BlackDuckSignatureScanner blackDuckSignatureScanner = Mockito.mock(BlackDuckSignatureScanner.class);

        DetectContext detectContext = Mockito.mock(DetectContext.class);
        Mockito.when(detectContext.getBean(any(Class.class), any(BlackDuckSignatureScannerOptions.class), any(ScanBatchRunner.class), any(), any())).thenReturn(blackDuckSignatureScanner);
        Mockito.when(detectContext.getBean(DirectoryManager.class)).thenReturn(directoryManager);
        Mockito.when(detectContext.getBean(CodeLocationNameManager.class)).thenReturn(codeLocationNameManager);
        Mockito.when(detectContext.getBean(ConnectionFactory.class)).thenReturn(null);

        NameVersion projectNameVersion = new NameVersion("name", "version");
        BlackDuckOnlineProperties blackDuckOnlineProperties = new BlackDuckOnlineProperties(null, false, false, false);
        ScanCommand scanCommand = new ScanCommand(null, null, false, null, null, 0, null, null, null, null, null, 0, false, null, blackDuckOnlineProperties, null, null, null, null, false, false, null, null);
        ScanBatchOutput scanBatchOutput = new ScanBatchOutput(Collections.singletonList(ScanCommandOutput.SUCCESS(projectNameVersion, null, null, scanCommand, null)));
        Mockito.when(blackDuckSignatureScanner.performScanActions(projectNameVersion, signatureScannerInstallationDirectory, null)).thenReturn(scanBatchOutput);

        BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, detectContext);

        testOffline(blackDuckSignatureScannerTool, scanBatchOutput, projectNameVersion);
        testOnline(blackDuckSignatureScannerTool, scanBatchOutput, projectNameVersion);
    }

    private void testOffline(BlackDuckSignatureScannerTool blackDuckSignatureScannerTool, ScanBatchOutput scanBatchOutput, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        SignatureScannerToolResult expected = SignatureScannerToolResult.createOfflineResult(scanBatchOutput);
        SignatureScannerToolResult actual = blackDuckSignatureScannerTool.runScanTool(null, null, projectNameVersion, Optional.empty());
        Assertions.assertTrue(areEqualResults(expected, actual));
    }

    private void testOnline(BlackDuckSignatureScannerTool blackDuckSignatureScannerTool, ScanBatchOutput scanBatchOutput, NameVersion projectNameVersion) throws IntegrationException, DetectUserFriendlyException {
        BlackDuckServerConfig blackDuckServerConfig = buildBlackDuckServerConfig();

        CodeLocationCreationService codeLocationCreationService = Mockito.mock(CodeLocationCreationService.class);
        NotificationTaskRange notificationTaskRange = new NotificationTaskRange(0, new Date(1000), new Date(1001));
        Mockito.when(codeLocationCreationService.calculateCodeLocationRange()).thenReturn(notificationTaskRange);
        CodeLocationCreationData<ScanBatchOutput> codeLocationCreationData = new CodeLocationCreationData<>(notificationTaskRange, scanBatchOutput);

        SignatureScannerToolResult expected = SignatureScannerToolResult.createOnlineResult(codeLocationCreationData);
        SignatureScannerToolResult actual = blackDuckSignatureScannerTool.runScanTool(codeLocationCreationService, blackDuckServerConfig, projectNameVersion, Optional.empty());
        Assertions.assertTrue(areEqualResults(expected, actual));
    }

    private BlackDuckServerConfig buildBlackDuckServerConfig() {
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        Gson gson = new Gson();
        ObjectMapper objectMapper = new ObjectMapper();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));
        String apiToken = "ZmMyYWM4OWEtOTA2MC00ODU1LTk5ZDYtNjRiN2IxMWM5YTUzOmM3YWU4MDlhLWUxOWMtNDc0Yy05N2QyLWJkZDkwNTM3NDEyMQ==";

        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = new BlackDuckServerConfigBuilder();
        blackDuckServerConfigBuilder.setIntEnvironmentVariables(intEnvironmentVariables);
        blackDuckServerConfigBuilder.setGson(gson);
        blackDuckServerConfigBuilder.setObjectMapper(objectMapper);
        blackDuckServerConfigBuilder.setExecutorService(executorService);
        blackDuckServerConfigBuilder.setLogger(logger);
        blackDuckServerConfigBuilder.setBlackDuckMediaTypeDiscovery(new BlackDuckMediaTypeDiscovery());
        blackDuckServerConfigBuilder.setApiToken(apiToken);
        blackDuckServerConfigBuilder.setTimeoutInSeconds(Integer.MAX_VALUE);
        blackDuckServerConfigBuilder.setUrl("https://int-hub02.dc1.lan");

        return blackDuckServerConfigBuilder.build();
    }

    private boolean areEqualResults(SignatureScannerToolResult result1, SignatureScannerToolResult result2) {
        boolean equalCreationData = (!result1.getCreationData().isPresent() && !result2.getCreationData().isPresent()) ||
                                        result1.getCreationData().isPresent() && result2.getCreationData().isPresent()
                                        && areEqualCreationData(result1.getCreationData().get(), result2.getCreationData().get());
        boolean equalOutputs = result1.getScanBatchOutput().equals(result2.getScanBatchOutput());
        boolean equalResults = result1.getResult().equals(result2.getResult());

        return equalCreationData && equalOutputs && equalResults;
    }

    private boolean areEqualCreationData(CodeLocationCreationData<ScanBatchOutput> data1, CodeLocationCreationData<ScanBatchOutput> data2) {
        return data1.getNotificationTaskRange().equals(data2.getNotificationTaskRange()) && data1.getOutput().equals(data2.getOutput());
    }
}
