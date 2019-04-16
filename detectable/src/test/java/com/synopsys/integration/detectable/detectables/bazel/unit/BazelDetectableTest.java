package com.synopsys.integration.detectable.detectables.bazel.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;

public class BazelDetectableTest {

    @Test
    public void testApplicable() {

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final BazelExtractor bazelExtractor = Mockito.mock(BazelExtractor.class);
        final BazelResolver bazelResolver = Mockito.mock(BazelResolver.class);
        final BazelDetectableOptions bazelDetectableOptions = Mockito.mock(BazelDetectableOptions.class);

        Mockito.when(bazelDetectableOptions.getTargetName()).thenReturn("target");

        final BazelDetectable d = new BazelDetectable( environment,  bazelExtractor,
         bazelResolver,  bazelDetectableOptions);

        assertTrue(d.applicable().getPassed());
    }
}
