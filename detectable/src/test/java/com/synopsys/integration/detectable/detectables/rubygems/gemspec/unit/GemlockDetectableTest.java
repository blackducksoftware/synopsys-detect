package com.synopsys.integration.detectable.detectables.rubygems.gemspec.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.GemlockExtractor;

public class GemlockDetectableTest {
    private static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";

    @Test
    public void testApplicable() {

        final GemlockExtractor gemlockExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, GEMFILE_LOCK_FILENAME)).thenReturn(new File(GEMFILE_LOCK_FILENAME));

        final GemlockDetectable detectable = new GemlockDetectable(environment, fileFinder, gemlockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
