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
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class ApkPackageManagerTest {
    @Test
    public void canParsePackages() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("this line has the is owned by substring\n");
        sb.append(" is owned by \n");
        // This is the one valid line; rest should be discarded
        sb.append("/usr/include/stdlib.h is owned by musl-dev-1.1.18-r3\n");
        sb.append("/usr/include/stdlib.h is owned by .musl-dev-1.1.18-r99\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        final ApkArchitectureResolver architectureResolver = Mockito.mock(ApkArchitectureResolver.class);
        Mockito.when(architectureResolver.resolveArchitecture(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of("x86_64"));

        ApkPackageManagerResolver apkPackageManagerResolver = new ApkPackageManagerResolver(architectureResolver);

        ClangPackageManagerInfo apk = new ClangPackageManagerFactory().apk();
        List<PackageDetails> pkgs = apkPackageManagerResolver.resolvePackages(apk, null, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("musl-dev", pkgs.get(0).getPackageName());
        assertEquals("1.1.18-r3", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void canParseArchitecture() throws ExecutableRunnerException {
        final ApkArchitectureResolver architectureResolver = new ApkArchitectureResolver();

        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.executeQuietly(null, "apk", Arrays.asList("info", "--print-arch"))).thenReturn(new ExecutableOutput(0, "x86_64\n", ""));

        ClangPackageManagerInfo apk = new ClangPackageManagerFactory().apk();
        Optional<String> architecture = architectureResolver.resolveArchitecture(apk, null, executableRunner);

        assertTrue(architecture.isPresent());
        assertEquals("x86_64", architecture.get());
    }
}