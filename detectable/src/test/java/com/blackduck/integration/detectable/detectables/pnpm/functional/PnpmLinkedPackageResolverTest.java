package com.blackduck.integration.detectable.detectables.pnpm.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.blackduck.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.blackduck.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.blackduck.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;

public class PnpmLinkedPackageResolverTest {
    PnpmLinkedPackageResolver pnpmLinkedPackageResolver = new PnpmLinkedPackageResolver(
        FunctionalTestFiles.asFile("/pnpm"),
        new PackageJsonFiles(new PackageJsonReader(new Gson()))
    );

    @Test
    public void testResolveVersionOfLinkedPackage() {
        Assertions.assertEquals("1.0.0", pnpmLinkedPackageResolver.resolveVersionOfLinkedPackage(null, "linkedProjectPackage"));
    }
}
