package com.synopsys.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NugetSolutionDetectableTest {
    @Test
    public void testApplicableForSolution() {
        final NugetInspectorResolver nugetInspectorManager = null;
        final NugetInspectorExtractor nugetInspectorExtractor = null;
        final NugetInspectorOptions nugetInspectorOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("test.sln");

        final NugetSolutionDetectable detectable = new NugetSolutionDetectable(environment, fileFinder, nugetInspectorManager, nugetInspectorExtractor, nugetInspectorOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
