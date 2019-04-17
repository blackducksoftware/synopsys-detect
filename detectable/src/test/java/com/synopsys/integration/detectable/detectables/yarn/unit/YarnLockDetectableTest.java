package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockExtractor;

public class YarnLockDetectableTest {
    private static final String YARN_LOCK_FILENAME = "yarn.lock";

    @Test
    public void testApplicable() {

        final YarnResolver yarnResolver = null;
        final YarnLockExtractor yarnLockExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, YARN_LOCK_FILENAME)).thenReturn(new File(YARN_LOCK_FILENAME));

        final YarnLockDetectable detectable = new YarnLockDetectable(environment, fileFinder, yarnResolver, yarnLockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
