package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfo;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkArchitectureResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ApkPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.NotOwnedByAnyPkgException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ApkPackageManagerTest {
    @Test
    public void canParsePackages() throws ExecutableRunnerException, NotOwnedByAnyPkgException {
        StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("this line has the is owned by substring\n");
        sb.append(" is owned by \n");
        sb.append("/usr/include/stdlib.h is owned by musl-dev-1.1.18-r3\n"); // This is the one valid line; rest should be discarded
        sb.append("/usr/include/stdlib.h is owned by .musl-dev-1.1.18-r99\n");
        String pkgMgrOwnedByOutput = sb.toString();

        ApkArchitectureResolver architectureResolver = Mockito.mock(ApkArchitectureResolver.class);
        Mockito.when(architectureResolver.resolveArchitecture(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of("x86_64"));

        ApkPackageManagerResolver apkPackageManagerResolver = new ApkPackageManagerResolver(architectureResolver);

        ClangPackageManagerInfo apk = new ClangPackageManagerInfoFactory().apk();
        List<PackageDetails> pkgs = apkPackageManagerResolver.resolvePackages(apk, null, null, pkgMgrOwnedByOutput);

        Assertions.assertEquals(1, pkgs.size());
        Assertions.assertEquals("musl-dev", pkgs.get(0).getPackageName());
        Assertions.assertEquals("1.1.18-r3", pkgs.get(0).getPackageVersion());
        Assertions.assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

    @Test
    public void canParseArchitecture() throws ExecutableRunnerException {
        final String exampleOutput = "x86_64\n";

        DetectableExecutableRunner executableRunner = Mockito.mock(DetectableExecutableRunner.class);
        Mockito.when(executableRunner.execute(null, "apk", Arrays.asList("info", "--print-arch"))).thenReturn(new ExecutableOutput(0, exampleOutput, ""));

        ApkArchitectureResolver architectureResolver = new ApkArchitectureResolver();
        Optional<String> architecture = architectureResolver.resolveArchitecture(new ClangPackageManagerInfoFactory().apk(), null, executableRunner);

        assertTrue(architecture.isPresent());
        Assertions.assertEquals("x86_64", architecture.get());
    }
}