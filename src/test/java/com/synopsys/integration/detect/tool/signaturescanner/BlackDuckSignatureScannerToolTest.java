package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.tool.detector.file.DetectDetectorFileFilter;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;

public class BlackDuckSignatureScannerToolTest {

    @Test
    public void testRunScanToolOnline() throws URISyntaxException, DetectUserFriendlyException {
        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = Mockito.mock(BlackDuckSignatureScannerOptions.class);
        Mockito.when(blackDuckSignatureScannerOptions.getParallelProcessors()).thenReturn(1);
        Mockito.when(blackDuckSignatureScannerOptions.getOfflineLocalScannerInstallPath()).thenReturn(null);
        Mockito.when(blackDuckSignatureScannerOptions.getOnlineLocalScannerInstallPath()).thenReturn(null);
        String userProvidedScannerInstallUrl = "https://int-hub02.dc1.lan/";
        Mockito.when(blackDuckSignatureScannerOptions.getUserProvidedScannerInstallUrl()).thenReturn(Optional.of(userProvidedScannerInstallUrl));

        DetectContext detectContext = Mockito.mock(DetectContext.class);

        DetectConfigurationFactory detectConfigurationFactory = Mockito.mock(DetectConfigurationFactory.class);
        Mockito.when(detectConfigurationFactory.createBlackDuckSignatureScannerOptions()).thenReturn(blackDuckSignatureScannerOptions);
        Mockito.when(detectContext.getBean(DetectConfigurationFactory.class)).thenReturn(detectConfigurationFactory);

        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        File signatureScannerInstallationDirectory = new File(BlackDuckSignatureScannerToolTest.class.getClassLoader().getResource("tool/signaturescanner/tools").toURI());
        Mockito.when(directoryManager.getPermanentDirectory()).thenReturn(signatureScannerInstallationDirectory);
        Mockito.when(detectContext.getBean(DirectoryManager.class)).thenReturn(directoryManager);

        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);
        Logger logger = LoggerFactory.getLogger(BlackDuckSignatureScannerToolTest.class);
        IntHttpClient intHttpClient = new IntHttpClient(new Slf4jIntLogger(logger), 0, true, ProxyInfo.NO_PROXY_INFO);
        Mockito.when(connectionFactory.createConnection(userProvidedScannerInstallUrl, new SilentIntLogger())).thenReturn(intHttpClient);
        Mockito.when(detectContext.getBean(ConnectionFactory.class)).thenReturn(connectionFactory);

        CodeLocationNameGenerator codeLocationNameGenerator = Mockito.mock(CodeLocationNameGenerator.class);
        CodeLocationNameManager codeLocationNameManager = Mockito.mock(CodeLocationNameManager.class);
        Mockito.when(detectContext.getBean(CodeLocationNameManager.class, codeLocationNameGenerator)).thenReturn(codeLocationNameManager);

        Mockito.when(detectContext.getBean(BlackDuckSignatureScanner.class, ));
    }
}
