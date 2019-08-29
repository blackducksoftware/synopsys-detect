package com.synopsys.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetProjectDetectable;

public class NugetProjectDetectableTest {
    private static final String FILENAME_PATTERN = "*.rproj";

    @Test
    public void testApplicable() {

        final NugetInspectorOptions nugetInspectorOptions = null;
        final NugetInspectorResolver nugetInspectorResolver = null;
        final NugetInspectorExtractor nugetInspectorExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFiles(Mockito.any(), Mockito.anyList())).thenReturn(Arrays.asList(new File(".")));

        final NugetProjectDetectable detectable = new NugetProjectDetectable(environment, fileFinder, nugetInspectorOptions, nugetInspectorResolver, nugetInspectorExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
