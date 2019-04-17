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
    private static final String GOFILE_FILENAME_PATTERN = "*.go";

    @Test
    public void testApplicable() {

        final GoResolver goResolver = null;
        final GoDepResolver goDepResolver = null;
        final GoDepExtractor goDepExtractor = null;
        final GoDepCliDetectableOptions goDepCliDetectableOptions = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        Mockito.when(environment.getDirectory()).thenReturn(new File("."));

        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFiles(dir, GOFILE_FILENAME_PATTERN)).thenReturn(Arrays.asList(new File(GOFILE_FILENAME_PATTERN)));

        final GoDepCliDetectable detectable = new GoDepCliDetectable(environment, fileFinder, goResolver, goDepResolver, goDepExtractor, goDepCliDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
