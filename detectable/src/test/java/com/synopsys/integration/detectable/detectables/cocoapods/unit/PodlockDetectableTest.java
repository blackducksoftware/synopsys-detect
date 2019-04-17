package com.synopsys.integration.detectable.detectables.cocoapods.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockDetectable;
import com.synopsys.integration.detectable.detectables.cocoapods.PodlockExtractor;

public class PodlockDetectableTest {
    private static final String PODFILE_LOCK_FILENAME = "Podfile.lock";

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final PodlockExtractor podlockExtractor = null;

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, PODFILE_LOCK_FILENAME)).thenReturn(new File(PODFILE_LOCK_FILENAME));

        final PodlockDetectable detectable = new PodlockDetectable(environment, fileFinder, podlockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
