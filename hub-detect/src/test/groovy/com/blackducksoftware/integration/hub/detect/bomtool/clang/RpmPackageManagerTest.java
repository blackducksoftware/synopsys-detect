package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class RpmPackageManagerTest {

    @Test
    public void testValid() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("glibc-headers-2.17-222.el7.x86_64\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final RpmPackageManager pkgMgr = new RpmPackageManager();
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.executeQuietly("rpm", Arrays.asList("-qf", "/usr/include/stdlib.h"))).thenReturn(new ExecutableOutput(0, pkgMgrOwnedByOutput, ""));

        final DependencyFileDetails dependencyFile = new DependencyFileDetails(false, new File("/usr/include/stdlib.h"));
        final List<PackageDetails> pkgs = pkgMgr.getPackages(executableRunner, new HashSet<>(), dependencyFile);
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

        final RpmPackageManager pkgMgr = new RpmPackageManager();
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.executeQuietly("rpm", Arrays.asList("-qf", "/usr/include/stdlib.h"))).thenReturn(new ExecutableOutput(0, pkgMgrOwnedByOutput, ""));

        final DependencyFileDetails dependencyFile = new DependencyFileDetails(false, new File("/usr/include/stdlib.h"));
        final List<PackageDetails> pkgs = pkgMgr.getPackages(executableRunner, new HashSet<>(), dependencyFile);
        assertEquals(0, pkgs.size());
    }

}
