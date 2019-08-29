package com.synopsys.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NpmPackageJsonParseDetectableTest {

    private static final String PACKAGE_JSON_FILENAME = "package.json";

    @Test
    public void testApplicable() {

        final PackageJsonExtractor packageJsonExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("package.json");

        final NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions = new NpmPackageJsonParseDetectableOptions(false);

        final NpmPackageJsonParseDetectable detectable = new NpmPackageJsonParseDetectable(environment, fileFinder, packageJsonExtractor, npmPackageJsonParseDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
