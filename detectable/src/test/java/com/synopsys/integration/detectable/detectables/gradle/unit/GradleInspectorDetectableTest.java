package com.synopsys.integration.detectable.detectables.gradle.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.GradleResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorDetectable;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorExtractor;
import com.synopsys.integration.detectable.detectables.gradle.inspection.GradleInspectorOptions;

public class GradleInspectorDetectableTest {
    public static final String BUILD_GRADLE_FILENAME = "build.gradle";

    @Test
    public void testApplicable() {

        final GradleResolver gradleResolver = null;
        final GradleInspectorResolver gradleInspectorResolver = null;
        final GradleInspectorExtractor gradleInspectorExtractor = null;
        final GradleInspectorOptions gradleInspectorOptions = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, BUILD_GRADLE_FILENAME)).thenReturn(new File(BUILD_GRADLE_FILENAME));

        final GradleInspectorDetectable detectable = new GradleInspectorDetectable(environment, fileFinder, gradleResolver, gradleInspectorResolver, gradleInspectorExtractor, gradleInspectorOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
