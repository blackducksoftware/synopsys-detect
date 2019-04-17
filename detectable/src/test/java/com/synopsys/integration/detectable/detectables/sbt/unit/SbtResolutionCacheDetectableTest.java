package com.synopsys.integration.detectable.detectables.sbt.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheExtractor;

public class SbtResolutionCacheDetectableTest {
    private static final String BUILD_SBT_FILENAME = "build.sbt";

    @Test
    public void testApplicable() {
        final SbtResolutionCacheExtractor sbtResolutionCacheExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, BUILD_SBT_FILENAME)).thenReturn(new File(BUILD_SBT_FILENAME));

        final SbtResolutionCacheDetectable detectable = new SbtResolutionCacheDetectable(environment, fileFinder, sbtResolutionCacheExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
