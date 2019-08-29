package com.synopsys.integration.detectable.detectables.pip.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PipInspectorDetectableTest {
    @Test
    public void testApplicableSetUpToolsFile() {

        final PythonResolver pythonResolver = null;
        final PipResolver pipResolver = null;
        final PipInspectorResolver pipInspectorResolver = null;
        final PipInspectorExtractor pipInspectorExtractor = null;
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = Mockito.mock(PipInspectorDetectableOptions.class);

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("setup.py");

        final PipInspectorDetectable detectable = new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor, pipInspectorDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }

    @Test
    public void testApplicableRequirementsFilePath() {

        final PythonResolver pythonResolver = null;
        final PipResolver pipResolver = null;
        final PipInspectorResolver pipInspectorResolver = null;
        final PipInspectorExtractor pipInspectorExtractor = null;
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = Mockito.mock(PipInspectorDetectableOptions.class);

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        final String[] requirementsFilePaths = new String[1];
        requirementsFilePaths[0] = "testReqtsPath";
        Mockito.when(pipInspectorDetectableOptions.getRequirementsFilePaths()).thenReturn(requirementsFilePaths);

        final PipInspectorDetectable detectable = new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor, pipInspectorDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
