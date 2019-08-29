package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class BitbakeDetectorTest {
    private static final String BUILD_ENV_NAME = "testBuildEnv";

    @Test
    public void testApplicable() {
        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final BitbakeExtractor bitbakeExtractor = null;
        final BashResolver bashResolver = null;

        final BitbakeDetectableOptions bitbakeDetectableOptions = new BitbakeDetectableOptions(BUILD_ENV_NAME, new String[] {}, new String[] { "testPkgName" });
        final FileFinder fileFinder = MockFileFinder.withFileNamed(BUILD_ENV_NAME);

        final BitbakeDetectable detectable = new BitbakeDetectable(environment, fileFinder, bitbakeDetectableOptions, bitbakeExtractor, bashResolver);

        assertTrue(detectable.applicable().getPassed());
    }
}
