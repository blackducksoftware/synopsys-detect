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
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GemspecParseDetectableTest {
    @Test
    public void testApplicable() {

        final GemspecParseExtractor gemspecParseExtractor = null;
        final GemspecParseDetectableOptions gemspecParseDetectableOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("test.gemspec");

        final GemspecParseDetectable detectable = new GemspecParseDetectable(environment, fileFinder, gemspecParseExtractor, gemspecParseDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
