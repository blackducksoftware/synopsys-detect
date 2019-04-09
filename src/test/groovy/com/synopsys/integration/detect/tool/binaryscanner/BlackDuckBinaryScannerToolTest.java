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
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerToolTest {


    @Test
    public void testShouldRunFalsePropertyNotSet() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn("");
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertFalse(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueFileNonExistent() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn("thisisnotafile");
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);
        
        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }


    @Test
    public void testShouldRunTruePropertySetToDirectory() {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn(".");
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueEverythingCorrect() throws IOException {
        final File binaryScanFile = Files.createTempFile("test", "binaryScanFile").toFile();
        binaryScanFile.deleteOnExit();
        assertTrue(binaryScanFile.canRead());
        assertTrue(binaryScanFile.exists());

        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn(binaryScanFile.getAbsolutePath());
        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem,
            null, detectConfiguration, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldFailOnDirectory() throws DetectUserFriendlyException {
        final DetectConfiguration detectConfiguration = Mockito.mock(DetectConfiguration.class);
        Mockito.when(detectConfiguration.getProperty(DetectProperty.DETECT_BINARY_SCAN_FILE, PropertyAuthority.None)).thenReturn(".");

        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem,
            null, detectConfiguration, null);
        final NameVersion projectNameVersion = new NameVersion("testName", "testVersion");

        final BinaryScanToolResult result = tool.performBinaryScanActions(projectNameVersion);

        assertFalse(result.isSuccessful());
    }
}
