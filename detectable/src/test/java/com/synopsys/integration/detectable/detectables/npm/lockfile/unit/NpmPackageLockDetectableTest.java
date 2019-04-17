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

    @Test
    public void testApplicable() {

        final NpmLockfileExtractor npmLockfileExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));
        
        final NpmLockfileOptions npmLockfileOptions = Mockito.mock(NpmLockfileOptions.class);
        Mockito.when(npmLockfileOptions.shouldIncludeDeveloperDependencies()).thenReturn(Boolean.TRUE);

        final NpmPackageLockDetectable detectable = new NpmPackageLockDetectable(environment, fileFinder, npmLockfileExtractor, npmLockfileOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
