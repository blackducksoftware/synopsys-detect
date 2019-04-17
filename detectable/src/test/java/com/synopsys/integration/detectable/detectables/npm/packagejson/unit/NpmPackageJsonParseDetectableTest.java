package com.synopsys.integration.detectable.detectables.npm.packagejson.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmLockfileOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectable;
import com.synopsys.integration.detectable.detectables.npm.packagejson.NpmPackageJsonParseDetectableOptions;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;

public class NpmPackageJsonParseDetectableTest {

    @Test
    public void testApplicable() {

        final PackageJsonExtractor packageJsonExtractor = null;

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        Mockito.when(environment.getDirectory()).thenReturn(new File("."));
        Mockito.when(fileFinder.findFile(Mockito.any(File.class), Mockito.anyString())).thenReturn(new File("."));

        final NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions = Mockito.mock(NpmPackageJsonParseDetectableOptions.class);
        Mockito.when(npmPackageJsonParseDetectableOptions.shouldIncludeDevDependencies()).thenReturn(Boolean.TRUE);

        final NpmPackageJsonParseDetectable detectable = new NpmPackageJsonParseDetectable(environment, fileFinder, packageJsonExtractor, npmPackageJsonParseDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
