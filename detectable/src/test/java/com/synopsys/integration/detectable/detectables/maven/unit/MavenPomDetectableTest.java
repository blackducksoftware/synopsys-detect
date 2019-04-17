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
    private static final String POM_FILENAME = "pom.xml";

    @Test
    public void testApplicable() {

        final MavenResolver mavenResolver = null;
        final MavenCliExtractor mavenCliExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, POM_FILENAME)).thenReturn(new File(POM_FILENAME));

        final MavenPomDetectable detectable = new MavenPomDetectable(environment, fileFinder, mavenResolver, mavenCliExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
