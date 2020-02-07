package com.synopsys.integration.detectable.detectables.conda.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectable;
import com.synopsys.integration.detectable.detectables.conda.CondaCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.conda.CondaCliExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class CondaCliDetectableTest {
    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("environment.yml");

        final CondaResolver condaResolver = null;
        final CondaCliExtractor condaExtractor = null;
        final CondaCliDetectable detectable = new CondaCliDetectable(environment, fileFinder, condaResolver, condaExtractor, new CondaCliDetectableOptions("example"));

        assertTrue(detectable.applicable().getPassed());
    }
}
