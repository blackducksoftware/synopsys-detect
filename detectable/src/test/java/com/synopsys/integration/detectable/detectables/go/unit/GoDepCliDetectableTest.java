package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.go.GoDepResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepCliDetectable;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepCliDetectableOptions;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepExtractor;

public class GoDepCliDetectableTest {

    @Test
    public void testApplicable() {

        final GoResolver goResolver = null;
        final GoDepResolver goDepResolver = null;
        final GoDepExtractor goDepExtractor = null;
        final GoDepCliDetectableOptions goDepCliDetectableOptions = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        Mockito.when(environment.getDirectory()).thenReturn(new File("."));

        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));
        Mockito.when(fileFinder.findFiles(Mockito.any(File.class), Mockito.anyString())).thenReturn(Arrays.asList(new File("test.txt")));

        final GoDepCliDetectable detectable = new GoDepCliDetectable(environment, fileFinder, goResolver, goDepResolver, goDepExtractor, goDepCliDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
