package com.synopsys.integration.detectable.detectables.npm.lockfile.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileExtractor;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NpmPackageLockDetectableTest {
    @Test
    public void testApplicable() {

        final NpmLockfileExtractor npmLockfileExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("package-lock.json");

        final NpmLockfileOptions npmLockfileOptions = new NpmLockfileOptions(false);

        final NpmPackageLockDetectable detectable = new NpmPackageLockDetectable(environment, fileFinder, npmLockfileExtractor, npmLockfileOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
