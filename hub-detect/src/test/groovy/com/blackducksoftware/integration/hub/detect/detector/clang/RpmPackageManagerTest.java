package com.blackducksoftware.integration.hub.detect.detector.clang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerBuilder;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerFactory;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.ClangPackageManagerInfo;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ApkArchitectureResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.ApkPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.detector.clang.packagemanager.dependencyfinder.RpmPackageManagerResolver;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class RpmPackageManagerTest {

    @Test
    public void testValid() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("glibc-headers-2.17-222.el7.x86_64\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver();
        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("glibc-headers", pkgs.get(0).getPackageName());
        assertEquals("2.17-222.el7", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void testInValid() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("file /opt/hub-detect/clang-repos/hello_world/hello_world.cpp is not owned by any package\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManagerResolver pkgMgr = new RpmPackageManagerResolver();
        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerFactory().rpm(), null, null, pkgMgrOwnedByOutput);

        assertEquals(0, pkgs.size());
    }

}
