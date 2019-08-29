package com.synopsys.integration.detectable.detectables.hex.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.Rebar3Resolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.hex.RebarDetectable;
import com.synopsys.integration.detectable.detectables.hex.RebarExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class RebarDetectableTest {
    @Test
    public void testApplicable() {

        final Rebar3Resolver rebar3Resolver = null;
        final RebarExtractor rebarExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("rebar.config");

        final RebarDetectable detectable = new RebarDetectable(environment, fileFinder, rebar3Resolver, rebarExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
