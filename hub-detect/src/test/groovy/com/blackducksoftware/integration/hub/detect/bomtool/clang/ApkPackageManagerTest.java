package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class ApkPackageManagerTest {

    private static final String PKG_MGR_OUTPUT = "garbage\nnonsense\nthis line has the is owned by substring\n/usr/include/stdlib.h is owned by musl-dev-1.1.18-r3\n";

    @Test
    public void test() throws ExecutableRunnerException {
        final ApkPackageManager pkgMgr = new ApkPackageManager();
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.executeQuietly("apk", "info", "--print-arch")).thenReturn(new ExecutableOutput(0, "x86_64\n", ""));
        Mockito.when(executableRunner.executeQuietly("apk", "info", "--who-owns", "/usr/include/stdlib.h")).thenReturn(new ExecutableOutput(0, PKG_MGR_OUTPUT, ""));

        final DependencyFileDetails dependencyFile = new DependencyFileDetails(false, new File("/usr/include/stdlib.h"));
        final List<PackageDetails> pkgs = pkgMgr.getPackages(executableRunner, new HashSet<>(), dependencyFile);
        assertEquals(1, pkgs.size());
        assertEquals("musl-dev", pkgs.get(0).getPackageName());
        assertEquals("1.1.18-r3", pkgs.get(0).getPackageVersion());
        assertEquals("x86_64", pkgs.get(0).getPackageArch());
    }

}
