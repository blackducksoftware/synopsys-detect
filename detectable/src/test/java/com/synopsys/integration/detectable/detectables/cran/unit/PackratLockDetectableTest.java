package com.synopsys.integration.detectable.detectables.cran.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PackratLockDetectableTest {
    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("packrat.lock");

        final PackratLockExtractor packratLockExtractor = null;

        final PackratLockDetectable detectable = new PackratLockDetectable(environment, fileFinder, packratLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
