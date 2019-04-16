package com.synopsys.integration.detectable.detectables.cocoapods;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;

public class PodlockDetectableTest {

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);
        final PodlockExtractor podlockExtractor = null;
        
        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final PodlockDetectable detectable = new PodlockDetectable(environment, fileFinder, podlockExtractor);

        assertTrue(detectable.applicable().getPassed());
    }
}
