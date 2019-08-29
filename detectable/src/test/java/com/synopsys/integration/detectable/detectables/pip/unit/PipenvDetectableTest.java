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
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

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

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed(targetFilename);

        return new PipenvDetectable(environment, pipenvDetectableOptions, fileFinder, pythonResolver, pipenvResolver, pipenvExtractor);
    }
}
