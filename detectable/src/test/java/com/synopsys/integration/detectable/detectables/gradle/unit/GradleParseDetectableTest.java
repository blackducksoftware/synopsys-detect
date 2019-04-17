package com.synopsys.integration.detectable.detectables.gradle.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseDetectable;
import com.synopsys.integration.detectable.detectables.gradle.parsing.GradleParseExtractor;

public class GradleParseDetectableTest {
    private static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @Test
    public void testApplicable() {

        final GradleParseExtractor gradleParseExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, BUILD_GRADLE_FILENAME)).thenReturn(new File(BUILD_GRADLE_FILENAME));

        final GradleParseDetectable detectable = new GradleParseDetectable(environment, fileFinder, gradleParseExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
