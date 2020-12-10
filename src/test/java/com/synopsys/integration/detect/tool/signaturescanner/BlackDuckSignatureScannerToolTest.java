package com.synopsys.integration.detect.tool.signaturescanner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.lifecycle.DetectContext;
import com.synopsys.integration.detect.tool.detector.file.DetectDetectorFileFilter;
import com.synopsys.integration.detect.workflow.DetectRun;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.file.DirectoryOptions;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;

public class BlackDuckSignatureScannerToolTest {

    @Test
    public void testRunScanTool() throws IOException, URISyntaxException {
        DirectoryManager directoryManager = Mockito.mock(DirectoryManager.class);
        File signatureScannerInstallationDirectory = new File(BlackDuckSignatureScannerToolTest.class.getClassLoader().getResource("tool/signaturescanner/tools").toURI());
        Mockito.when(directoryManager.getPermanentDirectory()).thenReturn(signatureScannerInstallationDirectory);
        BlackDuckSignatureScanner blackDuckSignatureScanner = Mockito.mock(BlackDuckSignatureScanner.class);

        BlackDuckSignatureScannerOptions blackDuckSignatureScannerOptions = Mockito.mock(BlackDuckSignatureScannerOptions.class);
        DetectContext detectContext = Mockito.mock(DetectContext.class);
        Mockito.when(detectContext.getBean(DirectoryManager.class)).thenReturn(directoryManager);
        Mockito.when(detectContext.getBean(BlackDuckSignatureScanner.class))
    }
}
