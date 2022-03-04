package com.synopsys.integration.detectable.detectables.bitbake.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.bitbake.collect.BuildFileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

@FunctionalTest
class BuildFileFinderTest {

    @Test
    void testDefault() {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder(), true, 10);
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_default");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(null, null);

        Optional<File> licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment);

        assertTrue(licensesManifestFile.isPresent());
        assertTrue(licensesManifestFile.get().isFile());
    }

    @Test
    void testCustomLicensesDirLocation() {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder(), true, 10);
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_custom");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(null, null);

        Optional<File> licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment);

        assertTrue(licensesManifestFile.isPresent());
        assertTrue(licensesManifestFile.get().isFile());
    }

    @Test
    void testArchSpecificLicensesDir() {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder(), true, 10);
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_arch");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment("testarch", null);

        Optional<File> licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment);

        assertTrue(licensesManifestFile.isPresent());
        assertTrue(licensesManifestFile.get().isFile());
    }
}
