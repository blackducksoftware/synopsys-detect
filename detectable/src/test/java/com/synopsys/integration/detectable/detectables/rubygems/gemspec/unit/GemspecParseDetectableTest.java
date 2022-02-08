package com.synopsys.integration.detectable.detectables.rubygems.gemspec.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectable;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.GemspecParseExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class GemspecParseDetectableTest {
    @Test
    public void testApplicable() {

        GemspecParseExtractor gemspecParseExtractor = null;
        GemspecParseDetectableOptions gemspecParseDetectableOptions = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed("test.gemspec");

        GemspecParseDetectable detectable = new GemspecParseDetectable(environment, fileFinder, gemspecParseExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
