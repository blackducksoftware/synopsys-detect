package com.synopsys.integration.detectable.detectables.bitbake.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.bitbake.BuildFileFinder;
import com.synopsys.integration.exception.IntegrationException;

@FunctionalTest
public class BuildFileFinderTest {

    @Test
    void testDefault() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = new File("src/test/resources/detectables/functional/bitbake/builddir_default");

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", true, 10);

        assertTrue(licensesManifestFile.isFile());
    }

    @Test
    void testCustomLicensesDirLocation() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = new File("src/test/resources/detectables/functional/bitbake/builddir_custom");

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", true, 10);

        assertTrue(licensesManifestFile.isFile());
    }

    @Test
    void testNoDeployLicensesDir() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = new File("src/test/resources/detectables/functional/bitbake/builddir_nodeploy");

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", true, 10);

        assertTrue(licensesManifestFile.isFile());
    }
}
