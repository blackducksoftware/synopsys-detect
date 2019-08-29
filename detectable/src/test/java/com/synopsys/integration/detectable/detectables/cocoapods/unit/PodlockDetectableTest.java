package com.synopsys.integration.detectable.detectables.cocoapods.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PodlockDetectableTest {
    @Test
    public void testApplicable() {
        final PodlockExtractor podlockExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("Podfile.lock");

        final PodlockDetectable detectable = new PodlockDetectable(environment, fileFinder, podlockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
