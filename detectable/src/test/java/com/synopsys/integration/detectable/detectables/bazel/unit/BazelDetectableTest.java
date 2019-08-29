package com.synopsys.integration.detectable.detectables.bazel.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;

public class BazelDetectableTest {

    @Test
    public void testApplicable() {
        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final BazelExtractor bazelExtractor = null;
        final BazelResolver bazelResolver = null;

        final BazelDetectableOptions bazelDetectableOptions = new BazelDetectableOptions("target", "");

        final BazelDetectable detectable = new BazelDetectable(environment, bazelExtractor, bazelResolver, bazelDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
