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
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GoVendorDetectableTest {

    @Test
    public void testApplicableVendor() {
        final GoVendorExtractor goVendorExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFilesNamed("vendor", "vendor.json");

        final GoVendorDetectable detectable = new GoVendorDetectable(environment, fileFinder, goVendorExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
