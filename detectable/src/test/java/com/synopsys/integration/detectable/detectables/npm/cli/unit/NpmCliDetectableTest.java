package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;

public class NpmCliDetectableTest {
    public static final String PACKAGE_JSON_FILENAME = "package.json";

    @Test
    public void testApplicable() {

        final NpmResolver npmResolver = null;
        final NpmCliExtractor npmCliExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, PACKAGE_JSON_FILENAME)).thenReturn(new File(PACKAGE_JSON_FILENAME));

        final NpmCliDetectable detectable = new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
