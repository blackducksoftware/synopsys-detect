package com.synopsys.integration.detectable.detectables.clang.unit;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectables.clang.dependencyfile.PackageDetails;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.ClangPackageManagerInfoFactory;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.DpkgVersionResolver;

public class DpkgPackageManagerTest {

    //TODO: Split into 2 tests - one for VersionResolution and one for FullResolution

    @Test
    public void test() throws ExecutableRunnerException, ExecutableRunnerException {
        StringBuilder sb = new StringBuilder();
        sb.append("garbage\n");
        sb.append("nonsense\n");
        sb.append("libc6-dev:amd64: /usr/include/stdlib.h\n");
        final String pkgMgrOwnedByOutput = sb.toString();

        sb = new StringBuilder();
        sb.append("Package: libc6-dev\n");
        sb.append("Status: install ok installed\n");
        sb.append("Priority: optional\n");
        sb.append("Section: libdevel\n");
        sb.append("Installed-Size: 18812\n");
        sb.append("Maintainer: Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>\n");
        sb.append("Architecture: amd64\n");
        sb.append("Multi-Arch: same\n");
        sb.append("Source: glibc\n");
        sb.append("Version: 2.27-3ubuntu1\n");
        sb.append("Provides: libc-dev\n");
        sb.append("Depends: libc6 (= 2.27-3ubuntu1), libc-dev-bin (= 2.27-3ubuntu1), linux-libc-dev\n");
        sb.append("Suggests: glibc-doc, manpages-dev\n");
        sb.append(
            "Breaks: binutils (<< 2.26), binutils-gold (<< 2.20.1-11), cmake (<< 2.8.4+dfsg.1-5), gcc-4.4 (<< 4.4.6-4), gcc-4.5 (<< 4.5.3-2), gcc-4.6 (<< 4.6.0-12), libhwloc-dev (<< 1.2-3), libjna-java (<< 3.2.7-4), liblouis-dev (<< 2.3.0-2), liblouisxml-dev (<< 2.4.0-2), libperl5.26 (<< 5.26.1-3), make (<< 3.81-8.1), pkg-config (<< 0.26-1)\n");
        sb.append("Conflicts: libc0.1-dev, libc0.3-dev, libc6.1-dev\n");
        sb.append("Description: GNU C Library: Development Libraries and Header Files\n");
        sb.append(" Contains the symlinks, headers, and object files needed to compile\n");
        sb.append(" and link programs which use the standard C library.\n");
        sb.append("Homepage: https://www.gnu.org/software/libc/libc.html\n");
        sb.append("Original-Maintainer: GNU Libc Maintainers <debian-glibc@lists.debian.org>\n");

        final String pkgMgrVersionOutput = sb.toString();

        String packageName = "libc6-dev";
        final ExecutableRunner executableRunner = Mockito.mock(ExecutableRunner.class);
        Mockito.when(executableRunner.execute(null, "dpkg", Arrays.asList("-s", packageName))).thenReturn(new ExecutableOutput(0, pkgMgrVersionOutput, ""));

        DpkgVersionResolver dpkgVersionResolver = new DpkgVersionResolver();
        final DpkgPackageManagerResolver pkgMgr = new DpkgPackageManagerResolver(dpkgVersionResolver);

        final List<PackageDetails> pkgs = pkgMgr.resolvePackages(new ClangPackageManagerInfoFactory().dpkg(), executableRunner, null, pkgMgrOwnedByOutput);

        assertEquals(1, pkgs.size());
        assertEquals("libc6-dev", pkgs.get(0).getPackageName());
        assertEquals("2.27-3ubuntu1", pkgs.get(0).getPackageVersion());
        assertEquals("amd64", pkgs.get(0).getPackageArch());
    }

}
