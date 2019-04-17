package com.synopsys.integration.detectable.detectables.cpan.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.CpanmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliDetectable;
import com.synopsys.integration.detectable.detectables.cpan.CpanCliExtractor;

public class CpanCliDetectableTest {
    private static final String MAKEFILE = "Makefile.PL";

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, MAKEFILE)).thenReturn(new File(MAKEFILE));

        final CpanResolver cpanResolver = null;
        final CpanmResolver cpanmResolver = null;
        final CpanCliExtractor cpanCliExtractor = null;

        final CpanCliDetectable detectable = new CpanCliDetectable(environment, fileFinder, cpanResolver, cpanmResolver, cpanCliExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
