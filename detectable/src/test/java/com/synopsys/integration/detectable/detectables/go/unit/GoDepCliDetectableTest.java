package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepExtractor;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepLockDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GoDepCliDetectableTest {
    @Test
    public void testApplicable() {

        final GoDepExtractor goDepExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("Gopkg.lock");

        final GoDepLockDetectable detectable = new GoDepLockDetectable(environment, fileFinder, goDepExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
