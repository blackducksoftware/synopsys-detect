package com.blackduck.integration.detectable.detectables.packagist.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.blackduck.integration.detectable.util.MockDetectableEnvironment;
import com.blackduck.integration.detectable.util.MockFileFinder;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.blackduck.integration.detectable.detectables.packagist.ComposerLockExtractor;

public class ComposerLockDetectableTest {
    @Test
    public void testApplicable() {

        ComposerLockExtractor composerLockExtractor = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFilesNamed("composer.lock", "composer.json");

        ComposerLockDetectable detectable = new ComposerLockDetectable(environment, fileFinder, composerLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
