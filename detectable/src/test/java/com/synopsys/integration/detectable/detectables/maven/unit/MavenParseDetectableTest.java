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
    private static final String POM_XML_FILENAME = "pom.xml";

    @Test
    public void testApplicable() {

        final MavenParseExtractor mavenParseExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, POM_XML_FILENAME)).thenReturn(new File(POM_XML_FILENAME));

        final MavenParseDetectable detectable = new MavenParseDetectable(environment, fileFinder, mavenParseExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
