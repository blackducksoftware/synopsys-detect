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

public class BitbakeDetectorTest {
    private static final String BUILD_ENV_NAME = "testBuildEnv";

    @Test
    public void testApplicable() {
        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final BitbakeDetectableOptions bitbakeDetectableOptions = Mockito.mock(BitbakeDetectableOptions.class);
        final BitbakeExtractor bitbakeExtractor = null;
        final BashResolver bashResolver = null;

        Mockito.when(bitbakeDetectableOptions.getBuildEnvName()).thenReturn(BUILD_ENV_NAME);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, BUILD_ENV_NAME)).thenReturn(new File(BUILD_ENV_NAME));

        final String[] pkgNames = { "testPkgName" };
        Mockito.when(bitbakeDetectableOptions.getPackageNames()).thenReturn(pkgNames);

        final BitbakeDetectable detectable = new BitbakeDetectable(environment, fileFinder, bitbakeDetectableOptions, bitbakeExtractor,
            bashResolver);

        assertTrue(detectable.applicable().getPassed());
    }
}
