package com.synopsys.integration.detect.tool.binaryscanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;

public class BlackDuckBinaryScannerToolTest {

    @Test
    public void testShouldRunFalseNonExistent() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn("thisisnotafile");
        
        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertFalse(shouldRunResponse);
    }


    @Test
    public void testShouldRunFalseDirectory() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn(".");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertFalse(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrue() throws IOException {
        final File binaryScanFile = Files.createTempFile("test", "binaryScanFile").toFile();
        binaryScanFile.deleteOnExit();
        assertTrue(binaryScanFile.canRead());
        assertTrue(binaryScanFile.exists());

        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn(binaryScanFile.getAbsolutePath());

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }
}
