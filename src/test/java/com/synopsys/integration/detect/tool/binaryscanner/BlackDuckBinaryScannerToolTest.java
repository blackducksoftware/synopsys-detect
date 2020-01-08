package com.synopsys.integration.detect.tool.binaryscanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.blackduck.codelocation.binaryscanner.BinaryScan;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.util.NameVersion;

public class BlackDuckBinaryScannerToolTest {

    @Test
    public void testShouldRunFalsePropertyNotSet() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions("", Arrays.asList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);
        boolean shouldRunResponse = tool.shouldRun();

        assertFalse(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueFileNonExistent() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions("thisisnotafile", Arrays.asList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTruePropertySetToDirectory() {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(".", Arrays.asList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldRunTrueEverythingCorrect() throws IOException {
        final File binaryScanFile = Files.createTempFile("test", "binaryScanFile").toFile();
        binaryScanFile.deleteOnExit();
        assertTrue(binaryScanFile.canRead());
        assertTrue(binaryScanFile.exists());

        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(binaryScanFile.getAbsolutePath(), Arrays.asList(""), "", "");

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(null, null, null, null, binaryScanOptions, null);

        boolean shouldRunResponse = tool.shouldRun();

        assertTrue(shouldRunResponse);
    }

    @Test
    public void testShouldFailOnDirectory() throws DetectUserFriendlyException {
        BinaryScanOptions binaryScanOptions = new BinaryScanOptions(".", Arrays.asList(""), "", "");

        final EventSystem eventSystem = Mockito.mock(EventSystem.class);

        final BlackDuckBinaryScannerTool tool = new BlackDuckBinaryScannerTool(eventSystem, null, null, null, binaryScanOptions, null);

        final NameVersion projectNameVersion = new NameVersion("testName", "testVersion");

        final BinaryScanToolResult result = tool.performBinaryScanActions(projectNameVersion);

        assertFalse(result.isSuccessful());
    }
}
