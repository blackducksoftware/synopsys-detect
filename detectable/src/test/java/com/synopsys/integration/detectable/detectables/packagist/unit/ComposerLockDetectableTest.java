package com.synopsys.integration.detectable.detectables.packagist.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockExtractor;

public class ComposerLockDetectableTest {
    private static final String COMPOSER_LOCK = "composer.lock";
    private static final String COMPOSER_JSON = "composer.json";

    @Test
    public void testApplicable() {

        final ComposerLockExtractor composerLockExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, COMPOSER_LOCK)).thenReturn(new File(COMPOSER_LOCK));
        Mockito.when(fileFinder.findFile(dir, COMPOSER_JSON)).thenReturn(new File(COMPOSER_JSON));

        final ComposerLockDetectable detectable = new ComposerLockDetectable(environment, fileFinder, composerLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
