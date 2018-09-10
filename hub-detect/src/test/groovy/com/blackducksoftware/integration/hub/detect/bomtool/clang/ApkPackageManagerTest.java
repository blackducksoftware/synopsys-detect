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

public class ApkPackageManagerTest {

    @Test
    public void test() throws ExecutableRunnerException {
        final StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("this line has the is owned by substring\n");
        sb.append(" is owned by \n");
        // This is the one valid line; rest should be discarded
        sb.append("/usr/include/stdlib.h is owned by musl-dev-1.1.18-r3\n");
        sb.append("/usr/include/stdlib.h is owned by .musl-dev-1.1.18-r99\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        File depFile = new File("/usr/include/stdlib.h");
        final ApkPackageManager pkgMgr = new ApkPackageManager();
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.executeQuietly(null, "apk", "info", "--print-arch")).thenReturn(new ExecutableOutput(0, "x86_64\n", ""));
        Mockito.when(executableRunner.executeQuietly(null,"apk", Arrays.asList("info", "--who-owns", depFile.getAbsolutePath()))).thenReturn(new ExecutableOutput(0, pkgMgrOwnedByOutput, ""));

        final DependencyFileDetails dependencyFile = new DependencyFileDetails(false, depFile );
        final List<PackageDetails> pkgs = pkgMgr.getPackages(null, executableRunner, new HashSet<>(), dependencyFile);
        assertEquals(1, pkgs.size());
        assertEquals("musl-dev", pkgs.get(0).getPackageName());
        assertEquals("1.1.18-r3", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

}
