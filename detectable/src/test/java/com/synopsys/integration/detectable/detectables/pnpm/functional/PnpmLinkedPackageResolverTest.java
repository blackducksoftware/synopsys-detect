package com.synopsys.integration.detectable.detectables.pnpm.functional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.pnpm.lockfile.process.PnpmLinkedPackageResolver;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonReader;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

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
