package com.synopsys.integration.detectable.detectables.rubygems.gemspec.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseExtractor;

public class GemspecParseDetectableTest {
    private static final String GEMSPEC_FILENAME = "*.gemspec";

    @Test
    public void testApplicable() {

        final GemspecParseExtractor gemspecParseExtractor = null;
        final GemspecParseDetectableOptions gemspecParseDetectableOptions = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, GEMSPEC_FILENAME)).thenReturn(new File("test.gemspec"));

        final GemspecParseDetectable detectable = new GemspecParseDetectable(environment, fileFinder, gemspecParseExtractor, gemspecParseDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
