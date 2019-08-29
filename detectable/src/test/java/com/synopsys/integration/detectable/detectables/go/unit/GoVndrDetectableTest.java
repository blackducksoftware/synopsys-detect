package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GoVndrDetectableTest {
    private static final String VNDR_CONF_FILENAME = "vendor.conf";

    @Test
    public void testApplicable() {

        final GoVndrExtractor goVndrExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("vendor.conf");

        final GoVndrDetectable detectable = new GoVndrDetectable(environment, fileFinder, goVndrExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
