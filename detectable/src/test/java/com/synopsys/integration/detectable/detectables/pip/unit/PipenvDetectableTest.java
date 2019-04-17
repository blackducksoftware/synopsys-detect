package com.synopsys.integration.detectable.detectables.pip.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvExtractor;

public class PipenvDetectableTest {
    private static final String PIPFILE_FILE_NAME = "Pipfile";
    private static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    @Test
    public void testApplicablePipfile() {
        final PipenvDetectable detectable = constructDetectable(PIPFILE_FILE_NAME);
        assertTrue(detectable.applicable().getPassed());
    }


    @Test
    public void testApplicablePipfileDotLock() {
        final PipenvDetectable detectable = constructDetectable(PIPFILE_DOT_LOCK_FILE_NAME);
        assertTrue(detectable.applicable().getPassed());
    }

    private PipenvDetectable constructDetectable(final String targetFilename) {
        final PipenvDetectableOptions pipenvDetectableOptions = null;
        final PythonResolver pythonResolver = null;
        final PipenvResolver pipenvResolver = null;
        final PipenvExtractor pipenvExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, targetFilename)).thenReturn(new File(targetFilename));

        return new PipenvDetectable(environment, pipenvDetectableOptions, fileFinder, pythonResolver, pipenvResolver, pipenvExtractor);
    }
}
