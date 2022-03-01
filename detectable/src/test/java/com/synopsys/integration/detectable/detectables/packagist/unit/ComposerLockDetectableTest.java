package com.synopsys.integration.detectable.detectables.packagist.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockDetectable;
import com.synopsys.integration.detectable.detectables.packagist.ComposerLockExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

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
