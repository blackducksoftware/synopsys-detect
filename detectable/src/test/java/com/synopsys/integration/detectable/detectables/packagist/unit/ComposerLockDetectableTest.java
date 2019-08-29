package com.synopsys.integration.detectable.detectables.packagist.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class ComposerLockDetectableTest {
    @Test
    public void testApplicable() {

        final ComposerLockExtractor composerLockExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFilesNamed("composer.lock", "composer.json");

        final ComposerLockDetectable detectable = new ComposerLockDetectable(environment, fileFinder, composerLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
