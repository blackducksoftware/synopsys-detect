package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractorOptions;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NpmCliDetectableTest {
    @Test
    public void testApplicable() {

        NpmResolver npmResolver = null;
        NpmCliExtractor npmCliExtractor = null;

        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = MockFileFinder.withFileNamed("package.json");

        NpmCliDetectable detectable = new NpmCliDetectable(
            environment,
            fileFinder,
            npmResolver,
            npmCliExtractor,
            new NpmCliExtractorOptions(EnumListFilter.fromExcluded(NpmDependencyType.DEV, NpmDependencyType.PEER), "")
        );

        assertTrue(detectable.applicable().getPassed());
    }
}
