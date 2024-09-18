package com.blackduck.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blackduck.integration.detectable.util.MockDetectableEnvironment;
import com.blackduck.integration.detectable.util.MockFileFinder;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectables.nuget.NugetInspectorOptions;
import com.blackduck.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.blackduck.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.blackduck.integration.detectable.detectables.nuget.NugetProjectDetectable;

public class NugetProjectDetectableTest {
    @Test
    public void testApplicableForRproj() {
        NugetInspectorOptions nugetInspectorOptions = null;
        NugetInspectorResolver nugetInspectorResolver = null;
        NugetInspectorExtractor nugetInspectorExtractor = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed("example.rproj");

        NugetProjectDetectable detectable = new NugetProjectDetectable(environment, fileFinder, nugetInspectorOptions, nugetInspectorResolver, nugetInspectorExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
