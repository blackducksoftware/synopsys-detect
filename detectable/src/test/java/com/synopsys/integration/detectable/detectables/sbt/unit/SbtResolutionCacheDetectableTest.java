package com.synopsys.integration.detectable.detectables.sbt.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectable;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheDetectableOptions;
import com.synopsys.integration.detectable.detectables.sbt.SbtResolutionCacheExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class SbtResolutionCacheDetectableTest {
    @Test
    public void testApplicable() {
        final SbtResolutionCacheExtractor sbtResolutionCacheExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("build.sbt");

        final SbtResolutionCacheDetectable detectable = new SbtResolutionCacheDetectable(environment, fileFinder, sbtResolutionCacheExtractor, new SbtResolutionCacheDetectableOptions("", "", 0));

        assertTrue(detectable.applicable().getPassed());
    }
}
