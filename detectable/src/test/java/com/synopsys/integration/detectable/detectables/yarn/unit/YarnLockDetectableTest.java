package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockExtractor;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockOptions;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class YarnLockDetectableTest {
    @Test
    public void testApplicable() {

        final YarnResolver yarnResolver = null;
        final YarnLockExtractor yarnLockExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("yarn.lock");

        final YarnLockDetectable detectable = new YarnLockDetectable(environment, fileFinder, yarnResolver, yarnLockExtractor, new YarnLockOptions(true));

        assertTrue(detectable.applicable().getPassed());
    }
}
