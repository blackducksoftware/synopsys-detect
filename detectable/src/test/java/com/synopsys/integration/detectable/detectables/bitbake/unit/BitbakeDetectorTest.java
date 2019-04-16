package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void testApplicable() {
        final DetectableEnvironment detectableEnvironment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final BitbakeDetectableOptions bitbakeDetectableOptions = Mockito.mock(BitbakeDetectableOptions.class);
        final BitbakeExtractor bitbakeExtractor = null;
        final BashResolver bashResolver = null;

        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));
        Mockito.when(detectableEnvironment.getDirectory()).thenReturn(new File("."));
        Mockito.when(bitbakeDetectableOptions.getBuildEnvName()).thenReturn("");

        final String[] pkgNames = { "" };
        Mockito.when(bitbakeDetectableOptions.getPackageNames()).thenReturn(pkgNames);

        final BitbakeDetectable d = new BitbakeDetectable( detectableEnvironment,  fileFinder, bitbakeDetectableOptions,  bitbakeExtractor,
         bashResolver);

        assertTrue(d.applicable().getPassed());
    }
}
