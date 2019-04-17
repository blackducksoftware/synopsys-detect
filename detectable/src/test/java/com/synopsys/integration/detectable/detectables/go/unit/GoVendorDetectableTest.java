package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorDetectable;
import com.synopsys.integration.detectable.detectables.go.vendor.GoVendorExtractor;

public class GoVendorDetectableTest {

    private static final String VENDOR_DIRNAME = "vendor";
    private static final String VENDOR_JSON_FILENAME = "vendor.json";

    @Test
    public void testApplicableVendor() {

        final File currentDir = new File(".");
        final File vendorDir = new File(VENDOR_DIRNAME);
        final File vendorFile = new File(vendorDir, VENDOR_JSON_FILENAME);

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        Mockito.when(environment.getDirectory()).thenReturn(currentDir);
        final GoVendorExtractor goVendorExtractor = null;

        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(currentDir, VENDOR_DIRNAME)).thenReturn(vendorDir);
        Mockito.when(fileFinder.findFile(vendorDir, VENDOR_JSON_FILENAME)).thenReturn(vendorFile);

        final GoVendorDetectable detectable = new GoVendorDetectable(environment, fileFinder, goVendorExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
