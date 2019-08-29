package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.go.GoDepResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepCliDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GoDepCliDetectableTest {
    @Test
    public void testApplicable() {

        final GoResolver goResolver = null;
        final GoDepResolver goDepResolver = null;
        final GoDepExtractor goDepExtractor = null;
        final GoDepCliDetectableOptions goDepCliDetectableOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("example.go");

        final GoDepCliDetectable detectable = new GoDepCliDetectable(environment, fileFinder, goResolver, goDepResolver, goDepExtractor, goDepCliDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
