package com.synopsys.integration.detectable.detectables.dart.unit;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.dart.pubdep.DartPubDepDetectable;

public class PubDepsDetectableTest {
    @Test
    public void testThrowExceptionWhenLockFilePresentButNotYaml() throws DetectableException {
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(null, "pubspec.yaml")).thenReturn(null);
        Mockito.when(fileFinder.findFile(null, "pubspec.lock")).thenReturn(new File(""));
        DartPubDepDetectable dartPubDepDetectable = new DartPubDepDetectable(new DetectableEnvironment(null), fileFinder, null, null, null, null);

        DetectableResult applicable = dartPubDepDetectable.applicable();
        Assertions.assertTrue(applicable.getPassed());

        DetectableResult extractable = dartPubDepDetectable.extractable();
        Assertions.assertTrue(extractable instanceof FileNotFoundDetectableResult);
    }
}
