package com.synopsys.integration.detectable.detectables.bazel.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;

public class BazelDetectableTest {

    @Test
    public void testApplicable() {
        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(new File("."), "WORKSPACE")).thenReturn(new File("src/test/resources/functional/bazel/WORKSPACE"));
        BazelExtractor bazelExtractor = null;
        BazelResolver bazelResolver = null;
        BazelDetectableOptions bazelDetectableOptions = new BazelDetectableOptions("target", null, null);
        BazelDetectable detectable = new BazelDetectable(environment, fileFinder, bazelExtractor, bazelResolver, bazelDetectableOptions.getTargetName().orElse(null));

        assertTrue(detectable.applicable().getPassed());
    }
}
