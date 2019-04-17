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

public class RebarDetectableTest {

    @Test
    public void testApplicable() {

        final Rebar3Resolver rebar3Resolver = null;
        final RebarExtractor rebarExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final RebarDetectable detectable = new RebarDetectable(environment, fileFinder, rebar3Resolver, rebarExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
