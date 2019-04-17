package com.synopsys.integration.detectable.detectables.conda.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliExtractor;

public class CondaCliDetectableTest {
    private static final String ENVIRONEMNT_YML = "environment.yml";

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, ENVIRONEMNT_YML)).thenReturn(new File(ENVIRONEMNT_YML));

        final CondaResolver condaResolver = null;
        final CondaCliExtractor condaExtractor = null;
        final CondaCliDetectable detectable = new CondaCliDetectable(environment, fileFinder, condaResolver, condaExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
