package com.synopsys.integration.detectable.detectables.go.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrDetectable;
import com.synopsys.integration.detectable.detectables.go.vendr.GoVndrExtractor;

public class GoVndrDetectableTest {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    @Test
    public void testApplicable() {

        final GoVndrExtractor goVndrExtractor = null;
        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(fileFinder.findFile(dir, VNDR_CONF_FILENAME)).thenReturn(new File(VNDR_CONF_FILENAME));

        final GoVndrDetectable detectable = new GoVndrDetectable(environment, fileFinder, goVndrExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
