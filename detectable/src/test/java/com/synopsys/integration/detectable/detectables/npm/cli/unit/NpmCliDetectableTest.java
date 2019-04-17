package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;

public class NpmCliDetectableTest {

    @Test
    public void testApplicable() {

        final NpmResolver npmResolver = null;
        final NpmCliExtractor npmCliExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final NpmCliDetectable detectable = new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor);
    }
}
