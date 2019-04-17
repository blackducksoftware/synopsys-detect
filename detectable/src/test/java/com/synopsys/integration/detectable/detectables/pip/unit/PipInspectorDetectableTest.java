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

public class PipInspectorDetectableTest {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";

    @Test
    public void testApplicableSetUpToolsFile() {

        final PythonResolver pythonResolver = null;
        final PipResolver pipResolver = null;
        final PipInspectorResolver pipInspectorResolver = null;
        final PipInspectorExtractor pipInspectorExtractor = null;
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = Mockito.mock(PipInspectorDetectableOptions.class);

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, SETUPTOOLS_DEFAULT_FILE_NAME)).thenReturn(new File(SETUPTOOLS_DEFAULT_FILE_NAME));
        
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
        Mockito.when(pipInspectorDetectableOptions.getRequirementsFilePath()).thenReturn("testReqtsPath");

        final PipInspectorDetectable detectable = new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor, pipInspectorDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
