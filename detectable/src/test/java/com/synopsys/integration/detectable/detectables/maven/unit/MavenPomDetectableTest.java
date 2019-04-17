package com.synopsys.integration.detectable.detectables.maven.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCliExtractor;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenPomDetectable;

public class MavenPomDetectableTest {

    @Test
    public void testApplicable() {

        final MavenResolver mavenResolver = null;
        final MavenCliExtractor mavenCliExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));


        final MavenPomDetectable detectable = new MavenPomDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
