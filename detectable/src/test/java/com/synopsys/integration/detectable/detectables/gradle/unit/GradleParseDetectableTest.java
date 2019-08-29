package com.synopsys.integration.detectable.detectables.gradle.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GradleParseDetectableTest {
    @Test
    public void testApplicable() {

        final GradleParseExtractor gradleParseExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("build.gradle");

        final GradleParseDetectable detectable = new GradleParseDetectable(environment, fileFinder, gradleParseExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
