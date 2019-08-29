package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.NpmPackageJsonDiscoverer;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliDetectable;
import com.synopsys.integration.detectable.detectables.npm.cli.NpmCliExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NpmCliDetectableTest {
    @Test
    public void testApplicable() {

        final NpmResolver npmResolver = null;
        final NpmCliExtractor npmCliExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("package.json");

        final NpmCliDetectable detectable = new NpmCliDetectable(environment, fileFinder, npmResolver, npmCliExtractor, new NpmPackageJsonDiscoverer(new Gson()));

        assertTrue(detectable.applicable().getPassed());
    }
}
