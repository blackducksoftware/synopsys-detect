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

    @Test
    public void testApplicable() {

        final GradleResolver gradleResolver = null;
        final GradleInspectorResolver gradleInspectorResolver = null;
        final GradleInspectorExtractor gradleInspectorExtractor = null;
        final GradleInspectorOptions gradleInspectorOptions = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final GradleInspectorDetectable detectable = new GradleInspectorDetectable(environment, fileFinder, gradleResolver, gradleInspectorResolver, gradleInspectorExtractor, gradleInspectorOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
