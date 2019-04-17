package com.synopsys.integration.detectable.detectables.maven.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseDetectable;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;

public class MavenParseDetectableTest {

    @Test
    public void testApplicable() {

        final MavenParseExtractor mavenParseExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final MavenParseDetectable detectable = new MavenParseDetectable(environment, fileFinder, mavenParseExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
