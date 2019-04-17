package com.synopsys.integration.detectable.detectables.cran.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.cran.PackratLockDetectable;
import com.synopsys.integration.detectable.detectables.cran.PackratLockExtractor;

public class PackratLockDetectableTest {
    private static final String PACKRATLOCK_FILE_NAME = "packrat.lock";

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, PACKRATLOCK_FILE_NAME)).thenReturn(new File(PACKRATLOCK_FILE_NAME));

        final PackratLockExtractor packratLockExtractor = null;

        final PackratLockDetectable detectable = new PackratLockDetectable(environment, fileFinder, packratLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
