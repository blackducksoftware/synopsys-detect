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

public class NpmPackageLockDetectableTest {

    private static final String PACKAGE_LOCK_JSON = "package-lock.json";

    @Test
    public void testApplicable() {

        final NpmLockfileExtractor npmLockfileExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, PACKAGE_LOCK_JSON)).thenReturn(new File(PACKAGE_LOCK_JSON));
        
        final NpmLockfileOptions npmLockfileOptions = Mockito.mock(NpmLockfileOptions.class);
        Mockito.when(npmLockfileOptions.shouldIncludeDeveloperDependencies()).thenReturn(Boolean.TRUE);

        final NpmPackageLockDetectable detectable = new NpmPackageLockDetectable(environment, fileFinder, npmLockfileExtractor, npmLockfileOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
