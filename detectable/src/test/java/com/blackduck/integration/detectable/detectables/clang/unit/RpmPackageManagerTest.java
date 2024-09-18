package com.blackduck.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.blackduck.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.resolver.NotOwnedByAnyPkgException;
import com.blackduck.integration.detectable.detectables.clang.packagemanager.resolver.RpmPackageManagerResolver;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class RpmPackageManagerTest {

    @Test
    public void testValidNoEpoch() throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        StringBuilder sb = new StringBuilder();
        sb.append("{ epoch: \"(none)\", name: \"boost-devel\", version: \"1.53.0-27.el7\", arch: \"x86_64\" }\n");
        String pkgMgrOwnedByOutput = sb.toString();

        RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("boost-devel", pkgs.get(0).getPackageName());
        assertEquals("1.53.0-27.el7", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testValidWithEpoch() throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        StringBuilder sb = new StringBuilder();
        sb.append("{ epoch: \"9\", name: \"boost-devel\", version: \"1.53.0-27.el7\", arch: \"x86_64\" }\n");
        String pkgMgrOwnedByOutput = sb.toString();

        RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("boost-devel", pkgs.get(0).getPackageName());
        assertEquals("9:1.53.0-27.el7", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testInValid() throws ExecutableRunnerException {
        StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("file /opt/hub-detect/clang-repos/hello_world/hello_world.cpp is not owned by any package\n");
        String pkgMgrOwnedByOutput = sb.toString();

        RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver(new Gson());
        assertThrows(NotOwnedByAnyPkgException.class, () ->
            pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().rpm(), null, null, pkgMgrOwnedByOutput)
        );
    }

    @Test
    public void testResolve() throws ExecutableRunnerException, NotOwnedByAnyPkgException {

        RpmPackageManagerResolver resolver = new RpmPackageManagerResolver(new Gson());
        ClangPackageManagerInfo currentPackageManager = null;
        DetectableExecutableRunner executableRunner = null;
        File workingDirectory = null;
        final String queryPackageOutput = "{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.17-222.el7\", arch: \"x86_64\" }\n" +
            "{ epoch: \"3\", name: \"test-package\", version: \"test-version\", arch: \"test_arch\" }\n";

        List<PackageDetails> pkgs = resolver.resolvePackages(currentPackageManager, executableRunner, workingDirectory, queryPackageOutput);
        assertEquals(2, pkgs.size());
        boolean foundGLibcHeaders = false;
        boolean foundTestPkg = false;
        for (PackageDetails pkg : pkgs) {
            if (pkg.getPackageName().equals("glibc-headers")) {
                foundGLibcHeaders = true;
                assertEquals("2.17-222.el7", pkg.getPackageVersion());
                assertEquals("x86_64", pkg.getPackageArch());
            }
            if (pkg.getPackageName().equals("test-package")) {
                foundTestPkg = true;
                assertEquals("3:test-version", pkg.getPackageVersion());
                assertEquals("test_arch", pkg.getPackageArch());
            }
        }
        assertTrue(foundGLibcHeaders);
        assertTrue(foundTestPkg);
    }

    @Test
    public void testParseSingleVariant() throws NotOwnedByAnyPkgException {
        RpmPackageManagerResolver resolver = new RpmPackageManagerResolver(new Gson());
        final String queryPackageOutputLine = "{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.28-101.el8\", arch: \"x86_64\" }";

        Optional<PackageDetails> pkg = resolver.generatePackageFromQueryOutputLine(queryPackageOutputLine);

        assertTrue(pkg.isPresent());
        assertEquals("glibc-headers", pkg.get().getPackageName());
        assertEquals("2.28-101.el8", pkg.get().getPackageVersion());
    }

    @Test
    public void testParseMultipleVariants() throws NotOwnedByAnyPkgException {
        RpmPackageManagerResolver resolver = new RpmPackageManagerResolver(new Gson());
        final String queryPackageOutputLine = "{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.28-101.el8\", arch: \"x86_64\" }{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.28-101.el8\", arch: \"i686\" }";

        Optional<PackageDetails> pkg = resolver.generatePackageFromQueryOutputLine(queryPackageOutputLine);

        assertTrue(pkg.isPresent());
        assertEquals("glibc-headers", pkg.get().getPackageName());
        assertEquals("2.28-101.el8", pkg.get().getPackageVersion());
    }

    @Test
    public void testParseBogusLine() throws NotOwnedByAnyPkgException {
        RpmPackageManagerResolver resolver = new RpmPackageManagerResolver(new Gson());
        final String queryPackageOutputLine = "{ epoch: \"(none)\", name: \"glibc-headers\", version: \"2.28-101.el8\", arch: \"x86_64{{{{{{\"  }";

        Optional<PackageDetails> pkg = resolver.generatePackageFromQueryOutputLine(queryPackageOutputLine);

        assertFalse(pkg.isPresent());
    }
}
