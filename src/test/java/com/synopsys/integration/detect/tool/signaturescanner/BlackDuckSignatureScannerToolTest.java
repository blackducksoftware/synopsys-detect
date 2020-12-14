package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchBuilder;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchOutput;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.ScanBatchRunner;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.BlackDuckOnlineProperties;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommand;
import com.synopsys.integration.blackduck.codelocation.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.tool.detector.file.DetectDetectorFileFilter;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckSignatureScannerToolTest {

    @Test
    public void testRunScanToolOffline() throws URISyntaxException, DetectUserFriendlyException, IOException, IntegrationException {
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = Mockito.mock(BlackDuckSignatureScannerOptions.class);
        Mockito.when(blackDuckSignatureScannerOptions.getParallelProcessors()).thenReturn(1);
        Mockito.when(blackDuckSignatureScannerOptions.getOfflineLocalScannerInstallPath()).thenReturn(Optional.empty());
        Mockito.when(blackDuckSignatureScannerOptions.getOnlineLocalScannerInstallPath()).thenReturn(Optional.empty());
        Mockito.when(blackDuckSignatureScannerOptions.getUserProvidedScannerInstallUrl()).thenReturn(Optional.empty());

        // TODO - see if we can avoid mocking this, other objects
        DetectContext detectContext = Mockito.mock(DetectContext.class);

        DetectConfigurationFactory detectConfigurationFactory = Mockito.mock(DetectConfigurationFactory.class);
        Mockito.when(detectConfigurationFactory.createBlackDuckSignatureScannerOptions()).thenReturn(blackDuckSignatureScannerOptions);
        Mockito.when(detectContext.getBean(DetectConfigurationFactory.class)).thenReturn(detectConfigurationFactory);

        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        File signatureScannerInstallationDirectory = new File(BlackDuckSignatureScannerToolTest.class.getClassLoader().getResource("tool/signaturescanner/tools").toURI());
        Mockito.when(directoryManager.getPermanentDirectory()).thenReturn(signatureScannerInstallationDirectory);
        Mockito.when(detectContext.getBean(DirectoryManager.class)).thenReturn(directoryManager);

        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Mockito.when(detectContext.getBean(ConnectionFactory.class)).thenReturn(connectionFactory);

        CodeLocationNameGenerator codeLocationNameGenerator = Mockito.mock(CodeLocationNameGenerator.class);
        CodeLocationNameManager codeLocationNameManager = Mockito.mock(CodeLocationNameManager.class);
        Mockito.when(detectContext.getBean(CodeLocationNameManager.class, codeLocationNameGenerator)).thenReturn(codeLocationNameManager);

        // copied from BlackDuckSignatuereScannerTool, trying to create replica ScanBatchRunner
        ExecutorService executorService = Executors.newFixedThreadPool(blackDuckSignatureScannerOptions.getParallelProcessors());
        IntEnvironmentVariables intEnvironmentVariables = IntEnvironmentVariables.includeSystemEnv();
        ScanBatchRunnerFactory scanBatchRunnerFactory = new ScanBatchRunnerFactory(intEnvironmentVariables, executorService);
        ScanBatchRunner scanBatchRunner = scanBatchRunnerFactory.withoutInstall(signatureScannerInstallationDirectory);

        BlackDuckSignatureScanner blackDuckSignatureScanner = Mockito.mock(BlackDuckSignatureScanner.class);
        Mockito.when(detectContext.getBean(BlackDuckSignatureScanner.class, blackDuckSignatureScannerOptions, scanBatchRunner, null, null)).thenReturn(blackDuckSignatureScanner);

        NameVersion projectNameVersion = new NameVersion("name", "version");
        BlackDuckOnlineProperties blackDuckOnlineProperties = new BlackDuckOnlineProperties(null, false, false, false);
        ScanCommand scanCommand = new ScanCommand(null, null, false, null, null, 0, null, null, null, null, null, 0, false, null, blackDuckOnlineProperties, null, null, null, null, false, false, null, null);
        ScanBatchOutput scanBatchOutput = new ScanBatchOutput(Collections.singletonList(ScanCommandOutput.SUCCESS(projectNameVersion, null, null, scanCommand, null)));
        Mockito.when(blackDuckSignatureScanner.performScanActions(projectNameVersion, signatureScannerInstallationDirectory, null)).thenReturn(scanBatchOutput);

        BlackDuckSignatureScannerTool blackDuckSignatureScannerTool = new BlackDuckSignatureScannerTool(blackDuckSignatureScannerOptions, detectContext);
        SignatureScannerToolResult expected = SignatureScannerToolResult.createOfflineResult(scanBatchOutput);
        SignatureScannerToolResult actual = blackDuckSignatureScannerTool.runScanTool(BlackDuckRunData.offline(), projectNameVersion, Optional.empty());
        Assertions.assertEquals(expected, actual);
    }
}
