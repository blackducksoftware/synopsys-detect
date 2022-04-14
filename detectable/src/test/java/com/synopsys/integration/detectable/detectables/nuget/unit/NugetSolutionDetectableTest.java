package com.synopsys.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NugetSolutionDetectableTest {
    @Test
    public void testApplicableForSolution() {
        NugetInspectorResolver nugetInspectorManager = null;
        NugetInspectorExtractor nugetInspectorExtractor = null;
        NugetInspectorOptions nugetInspectorOptions = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed("test.sln");

        NugetSolutionDetectable detectable = new NugetSolutionDetectable(environment, fileFinder, nugetInspectorManager, nugetInspectorExtractor, nugetInspectorOptions);

        assertTrue(detectable.applicable().getPassed());
    }

    @Test
    public void notApplicableForPodfile() {
        NugetInspectorResolver nugetInspectorManager = null;
        NugetInspectorExtractor nugetInspectorExtractor = null;
        NugetInspectorOptions nugetInspectorOptions = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed("podfile.lock");

        NugetSolutionDetectable detectable = new NugetSolutionDetectable(environment, fileFinder, nugetInspectorManager, nugetInspectorExtractor, nugetInspectorOptions);

        assertFalse(detectable.applicable().getPassed());
    }
}

