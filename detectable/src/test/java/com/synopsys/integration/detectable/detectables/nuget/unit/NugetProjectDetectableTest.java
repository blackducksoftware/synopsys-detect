package com.synopsys.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

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
