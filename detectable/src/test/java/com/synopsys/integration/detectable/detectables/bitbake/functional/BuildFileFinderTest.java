package com.synopsys.integration.detectable.detectables.bitbake.functional;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.bitbake.BuildFileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeEnvironment;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.exception.IntegrationException;

@FunctionalTest
public class BuildFileFinderTest {

    @Test
    void testDefault() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_default");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(null, null);

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment, true, 10);

        assertTrue(licensesManifestFile.isFile());
    }

    @Test
    void testCustomLicensesDirLocation() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_custom");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment(null, null);

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment, true, 10);

        assertTrue(licensesManifestFile.isFile());
    }

    @Test
    void testArchSpecificLicensesDir() throws IntegrationException {
        BuildFileFinder finder = new BuildFileFinder(new SimpleFileFinder());
        File buildDir = FunctionalTestFiles.asFile("/bitbake/builddir_arch");
        BitbakeEnvironment bitbakeEnvironment = new BitbakeEnvironment("testarch", null);

        File licensesManifestFile = finder.findLicenseManifestFile(buildDir, "targetimage", bitbakeEnvironment, true, 10);

        assertTrue(licensesManifestFile.isFile());
    }
}
