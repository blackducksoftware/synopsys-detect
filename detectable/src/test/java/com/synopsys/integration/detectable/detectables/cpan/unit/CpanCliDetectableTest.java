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
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class CpanCliDetectableTest {
    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("Makefile.PL");

        final CpanResolver cpanResolver = null;
        final CpanmResolver cpanmResolver = null;
        final CpanCliExtractor cpanCliExtractor = null;

        final CpanCliDetectable detectable = new CpanCliDetectable(environment, fileFinder, cpanResolver, cpanmResolver, cpanCliExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
